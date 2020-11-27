package com.rebeccablum.naggle.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rebeccablum.naggle.databinding.FragmentNagListBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class NagListFragment : Fragment() {

    private lateinit var binding: FragmentNagListBinding

    private val viewModel: NagListViewModel by sharedViewModel()
    private val args: NagListFragmentArgs by navArgs()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val nagId = args.nagId
        if (nagId != -1) {
            viewModel.onOpenToNag(nagId)
        }
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
