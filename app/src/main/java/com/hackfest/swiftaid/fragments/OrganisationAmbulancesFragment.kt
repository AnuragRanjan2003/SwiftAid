package com.hackfest.swiftaid.fragments

import android.os.Bundle
import android.provider.Settings.Global.putString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hackfest.swiftaid.R
import com.hackfest.swiftaid.adapter.AmbulancesAdapter
import com.hackfest.swiftaid.databinding.FragmentOrganisationAmbulancesBinding
import com.hackfest.swiftaid.models.Ambulance
import com.hackfest.swiftaid.viewModels.AmbulanceViewModel
import com.hackfest.swiftaid.viewModels.factory.AmbulanceViewModelFactory


class OrganisationAmbulancesFragment : Fragment() {

    private lateinit var binding: FragmentOrganisationAmbulancesBinding

    private lateinit var viewModel: AmbulanceViewModel
    private lateinit var viewModelFactory: AmbulanceViewModelFactory

    private var orgAuthID: String? = null
    private val bundle = Bundle()
    private val ambulanceList = ArrayList<Ambulance>()
    lateinit var adapter: AmbulancesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOrganisationAmbulancesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orgAuthID = arguments?.getString("orgAuthID").toString()
        binding.fabAddAmbulance.setOnClickListener {
            bundle.apply {
                putString("orgAuthID", orgAuthID)
            }
            findNavController().navigate(
                R.id.action_organisationAmbulancesFragment_to_ambulanceDetailsFragment2,
                bundle)

        }

        binding.rvAmbulance.layoutManager = LinearLayoutManager(context)
        adapter = AmbulancesAdapter()
        binding.rvAmbulance.adapter = adapter

        viewModelFactory = AmbulanceViewModelFactory(orgAuthID)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AmbulanceViewModel::class.java)

        viewModel.allAmbulances.observe(viewLifecycleOwner, Observer {
            for (i in it) {
                if (i.orgAuthID == orgAuthID) {
                    Log.d("Data added", "${i.driverName}")
                    ambulanceList.add(i)
                }
            }
            adapter.updateAmbulanceList(ambulanceList)
        })
        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })


    }

    private fun filterList(query: String?) {

        if (query != null) {
            val filteredList = ArrayList<Ambulance>()
            for (i in ambulanceList) {
                if (i.driverName?.toLowerCase() == query) {
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show()
            } else {
                adapter.setFilteredList(filteredList)
            }
        }

    }
}

