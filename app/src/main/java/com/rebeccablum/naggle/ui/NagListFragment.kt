package com.rebeccablum.naggle.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.rebeccablum.naggle.databinding.FragmentNagListBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class NagListFragment : Fragment() {

    private lateinit var binding: FragmentNagListBinding

    private val viewModel: NagListViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNagListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val adapter = NagListAdapter(viewModel)
        subscribeUi(adapter)
        binding.nagList.adapter = adapter
        binding.nagList.layoutManager = LinearLayoutManager(requireActivity())

        return binding.root
    }

    private fun subscribeUi(adapter: NagListAdapter) {
        viewModel.nags.observe(viewLifecycleOwner, Observer { adapter.submitList(it) })
        viewModel.actionAddEditNag.observe(viewLifecycleOwner, Observer { showAddNagDialog() })
    }

    private fun showAddNagDialog() {
        val dialogFragment = AddEditNagDialogFragment()
        dialogFragment.show(
            requireActivity().supportFragmentManager,
            AddEditNagDialogFragment::class.java.simpleName
        )
    }
}
