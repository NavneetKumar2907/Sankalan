package com.ducs.sankalan.fragmentsadmin

import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ducs.sankalan.adapter.AdminRegEventAdapter
import com.ducs.sankalan.data.RegisteredEvents
import com.ducs.sankalan.databinding.FragmentRegisteredEventAdminBinding
import com.ducs.sankalan.model.AdminViewModel
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream


class RegisteredEventAdminFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var registeredEvents: FragmentRegisteredEventAdminBinding
    private val regViewModel by activityViewModels<AdminViewModel>()
    private var filRes = listOf<RegisteredEvents>()
    private var hssfWorkbook = HSSFWorkbook()


    val eventNames = arrayListOf<String>()
    var uniqueAllRegEvent = listOf<RegisteredEvents>()
    private var permission = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            checkPermision()
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        registeredEvents = FragmentRegisteredEventAdminBinding.inflate(layoutInflater)
        return registeredEvents.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registeredEvents.registeredEventsList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }

        // Event list
        regViewModel.getEvent().observe(viewLifecycleOwner, Observer {
            for (e in it) {
                eventNames.add(e.eventName)
            }
            val spinnerArrayAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, eventNames)
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            registeredEvents.eventRegisteredEventSpinner.adapter = spinnerArrayAdapter
        })

        registeredEvents.eventRegisteredEventSpinner.onItemSelectedListener = this

        regViewModel.regEvent.observe(viewLifecycleOwner, Observer { ev ->
            uniqueAllRegEvent = ev
        })
        loadListener()

    }

    private fun loadListener() {
        registeredEvents.printRegEvent.setOnClickListener {
            try {
                if (filRes.isEmpty()) {
                    Toast.makeText(requireContext(), "No Registration.", Toast.LENGTH_SHORT).show()
                } else {
                    printPDF()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                        val contentValues = ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, "/${filRes[0].eventName}.xls")
                            put(MediaStore.MediaColumns.MIME_TYPE, "application/excel")
                            put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_DOWNLOADS)
                        }
                        val resolver = requireContext().contentResolver
                        val uri = resolver.insert(
                            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                            contentValues
                        )
                        if (uri != null) {

                            try {
                                val fileOutputStream = resolver.openOutputStream(uri)
                                hssfWorkbook.write(fileOutputStream)
                                fileOutputStream?.flush()
                                fileOutputStream?.close()
                                Toast.makeText(
                                    requireContext(),
                                    "Saved at: ${Environment.DIRECTORY_DOWNLOADS} ",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }catch (e:Exception){
                                Log.w("Error: ",e.message.toString())
                            }

                        }
                    } else {
                        if (permission) {
                            WriteToFile()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Require Permission.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                }
            } catch (e: Exception) {
                Log.w("Error", e.message.toString())
                Toast.makeText(requireContext(), "Empty Selection!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun WriteToFile() {
        val filePath =
            File(Environment.getExternalStorageDirectory().toString() + "/${filRes[0].eventName}.xls")

        try {
            if (!filePath.exists()) {
                filePath.createNewFile()
            }
            val fileOutputStream = FileOutputStream(filePath)
            hssfWorkbook.write(fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            Toast.makeText(requireContext(), "Saved at: $filePath", Toast.LENGTH_SHORT).show()
            Log.w("File Path: ", filePath.toString())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun loadAdapter(listRegEvent: List<RegisteredEvents>) {

        registeredEvents.registeredCount.text = listRegEvent.size.toString()
        registeredEvents.registeredEventsList.adapter =
            AdminRegEventAdapter(listRegEvent, regViewModel.userData.value)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        filRes = uniqueAllRegEvent.filter {
            it.eventName == eventNames[p2]
        }
        loadListener()

        if (regViewModel.getEvent().value?.find {
                it.eventName == eventNames[p2]
            }?.Team == "Team"
        ) {
            filRes = filRes.distinctBy { it.teamName }
        }

        Log.w("filter", filRes.toString())
        loadAdapter(filRes)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    private fun printPDF() {
        try {
            hssfWorkbook = HSSFWorkbook()
            val hssfSheet = hssfWorkbook.createSheet("Custom Sheet")

            var r = 0
            var c = 0
            var hssfRow = hssfSheet.createRow(r)
            var hssfCell = hssfRow.createCell(c)
            var count = 0
            val excelHead = listOf("TeamName", "Name", "email", "Phone")
            var pass = false
            if (filRes[0].teamName == "") {
                //Team
                pass = true

            }
            for (e in excelHead) {
                if (pass && (e == "TeamName")) {
                    continue
                }
                count += 1
                hssfCell.setCellValue(e)
                c += 1
                hssfCell = hssfRow.createCell(c)
            }


            r += 1
            hssfRow = hssfSheet.createRow(r)
            c = 0
            hssfCell = hssfRow.createCell(c)

            for (re in filRes) {

                //Iteration over registered events
                if (pass) {
                    //Individual event
                    val userDetail = regViewModel.userData.value?.find {
                        it.uid == re.individual
                    }
                    hssfCell.setCellValue(userDetail?.name)//Set name
                    c += 1
                    hssfCell = hssfRow.createCell(c)
                    hssfCell.setCellValue(userDetail?.email)//Set Email
                    c += 1
                    hssfCell = hssfRow.createCell(c)
                    hssfCell.setCellValue(userDetail?.mobile)//Set Phone
                    r += 1
                    hssfRow = hssfSheet.createRow(r)
                    c = 0
                    hssfCell = hssfRow.createCell(c)
                    continue
                }
                //Team
                hssfCell.setCellValue(re.teamName) //Team Name
                c += 1
                hssfCell = hssfRow.createCell(c)    //Next Cell
                val memberIterator = listOf<String>(
                    re.members.member1,
                    re.members.member2,
                    re.members.member3,
                    re.members.member4
                )

                for (m in memberIterator) {
                    if (m == "") {
                        break
                    }
                    val userDetail = regViewModel.userData.value?.find {
                        it.email.lowercase() == m.lowercase()
                    }//User Detail

                    hssfCell.setCellValue(userDetail?.name)//Name
                    c += 1
                    hssfCell = hssfRow.createCell(c) //Next Cell
                    hssfCell.setCellValue(userDetail?.email)//Email
                    c += 1
                    hssfCell = hssfRow.createCell(c) //Next Cell
                    hssfCell.setCellValue(userDetail?.mobile)//MObile

                    r += 1
                    hssfRow = hssfSheet.createRow(r)//Next Row
                    c = 0
                    hssfCell = hssfRow.createCell(c) //Next Cell
                    c += 1
                    hssfCell = hssfRow.createCell(c) //Nexr member name cell
                }
                c = 0
                hssfCell = hssfRow.createCell(c)


            }//End

        } catch (e: Exception) {
            Log.w("Error", e.message.toString())
        }

    }

    //Check for Permission
    fun checkPermision() {
        // Register the permissions callback, which handles the user's response to the
// system permissions dialog. Save the return value, an instance of
// ActivityResultLauncher. You can use either a val, as shown in this snippet,
// or a lateinit var in your onAttach() or onCreate() method.
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    permission = true
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    Toast.makeText(
                        requireContext(),
                        "Please Grant Permission to Save sheet.!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        when (PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) -> {
                // You can use the API that requires the permission.
                permission = true
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }
}