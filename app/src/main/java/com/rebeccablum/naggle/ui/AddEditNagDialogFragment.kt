package com.rebeccablum.naggle.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.rebeccablum.naggle.R
import com.rebeccablum.naggle.databinding.FragmentAddNagDialogBinding
import com.rebeccablum.naggle.models.Priority
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddEditNagDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentAddNagDialogBinding
    private lateinit var adapter: ArrayAdapter<Priority>

    private val addEditViewModel: NagListViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_AppCompat_Dialog)
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddNagDialogBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = addEditViewModel
        binding.priorityDropdownMenu.setText(
            addEditViewModel.priorityString.value ?: Priority.NORMAL.name, false
        )
        adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            Priority.values()
        )
        binding.priorityDropdownMenu.setAdapter(adapter)

        addEditViewModel.actionDismiss.observe(this, Observer { dismiss() })
        addEditViewModel.errorDisplay.observe(this, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        })

        return binding.root
    }
}