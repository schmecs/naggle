package com.rebeccablum.naggle.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rebeccablum.naggle.databinding.FragmentNagListBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class NagListFragment : Fragment() {

    private lateinit var binding: FragmentNagListBinding

    private val viewModel: NagListViewModel by sharedViewModel()
    private val args: NagListFragmentArgs by navArgs()

    override fun onStart() {
        super.onStart()
        val nagId = args.nagId
        if (nagId != NO_DESTINATION) {
            findNavController().navigate(NagListFragmentDirections.startAddEditNagFragment(nagId))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNagListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val adapter = NagListAdapter(viewModel)
        subscribeUi(adapter)
        binding.nagList.adapter = adapter
        binding.nagList.layoutManager = LinearLayoutManager(requireActivity())

        return binding.root
    }

    private fun subscribeUi(adapter: NagListAdapter) {
        viewModel.nags.observe(viewLifecycleOwner, { adapter.submitList(it) })
        viewModel.actionAddEditNag.observe(viewLifecycleOwner, { id ->
            showAddNagDialog(id)
        })
    }

    private fun showAddNagDialog(id: Int) {
        findNavController().navigate(NagListFragmentDirections.startAddEditNagFragment(id))
    }
}
