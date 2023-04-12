package com.hackfest.swiftaid.fragments

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings.Global.putString
import android.util.Log
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.SearchView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.adapter.AmbulancesAdapter
import com.hackfest.swiftaid.databinding.FragmentOrganisationAmbulancesBinding
import com.hackfest.swiftaid.models.Ambulance
import com.hackfest.swiftaid.viewModels.AmbulanceViewModel
import com.hackfest.swiftaid.viewModels.factory.AmbulanceViewModelFactory
import java.util.*
import kotlin.collections.ArrayList


class OrganisationAmbulancesFragment : Fragment() {

    private lateinit var binding: FragmentOrganisationAmbulancesBinding
    private lateinit var auth : FirebaseAuth

    private lateinit var viewModel: AmbulanceViewModel
    private lateinit var viewModelFactory: AmbulanceViewModelFactory

    private var orgAuthID: String? = null
    private val bundle = Bundle()
    private val ambulanceList = ArrayList<Ambulance>()
    lateinit var adapter: AmbulancesAdapter
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        orgAuthID = arguments?.getString("orgAuthID").toString()
//        e("org",orgAuthID.toString())
//        auth = Firebase.auth
//        orgAuthID = auth.currentUser!!.uid
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOrganisationAmbulancesBinding.inflate(inflater, container, false)

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_dialog)
        if(dialog.window != null){
            dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        }
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        orgAuthID = auth.currentUser?.uid


//        orgAuthID = auth.currentUser!!.uid
        e("orgOnView",orgAuthID.toString())

        binding.fabAddAmbulance.setOnClickListener {

            findNavController().navigate(
                R.id.action_organisationAmbulancesFragment_to_ambulanceDetailsFragment2,bundle.apply {
                    putString("orgAuthID",orgAuthID)
                })
        }
        val nc = findNavController()
        binding.btnSignOut.setOnClickListener {
            auth.signOut()
            nc.navigate(R.id.splashFragment)
        }

        binding.rvAmbulance.layoutManager = LinearLayoutManager(context)
        adapter = AmbulancesAdapter{
            val bundle = Bundle()
            bundle.putString("ambId",it)
            findNavController().navigate(R.id.action_organisationAmbulancesFragment_to_ambulanceMapFragment,bundle)
        }
        binding.rvAmbulance.adapter = adapter

        viewModelFactory = AmbulanceViewModelFactory(orgAuthID)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AmbulanceViewModel::class.java)

        viewModel.allAmbulances.observe(viewLifecycleOwner, Observer {
            ambulanceList.clear()
            e("orgAuthID observe",orgAuthID.toString())
            for (i in it) {
                if (i.orgAuthID == orgAuthID) {

                    e("Data added", "${i.driverName}")
                    ambulanceList.add(i)
                }
            }
            e("list","$ambulanceList")
            adapter.updateAmbulanceList(ambulanceList)
            dialog.dismiss()
        })
        binding.searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Call your function to exit the app
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun filterList(query: String?) {

        if (query != null) {
            val filteredList = ArrayList<Ambulance>()
            for (i in ambulanceList) {
                e("ambulance:Search","${i.driverName}")
                if (i.driverName?.toLowerCase(Locale.getDefault())!!.contains(query.toLowerCase(Locale.getDefault()))) {
                    e("ambulance:SearchDriver","${i.driverName}")
                    filteredList.add(i)
                }
            }
            adapter.notifyDataSetChanged()

            if (filteredList.isEmpty() && ambulanceList.isNotEmpty()) {
                Toast.makeText(context,"No data found",Toast.LENGTH_SHORT).show()
            } else {
                e("ambulance:SearchDriverList","error in search")
                adapter.setFilteredList(filteredList)
            }
        }

    }
}

