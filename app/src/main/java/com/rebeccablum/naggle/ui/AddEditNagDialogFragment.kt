package com.rebeccablum.naggle.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.google.android.material.datepicker.MaterialDatePicker
import com.rebeccablum.naggle.R
import com.rebeccablum.naggle.databinding.FragmentAddEditNagDialogBinding
import com.rebeccablum.naggle.models.Priority
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class AddEditNagDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentAddEditNagDialogBinding
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
        binding = FragmentAddEditNagDialogBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = addEditViewModel
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

        observeDateAndTimeClicks()

        return binding.root
    }

    private fun observeDateAndTimeClicks() {
        addEditViewModel.actionEditDate.observe(this, Observer {
            val builder = MaterialDatePicker.Builder.datePicker()
            val currentTimeInMillis = Calendar.getInstance().timeInMillis
            builder.setSelection(currentTimeInMillis)
            builder.setTheme(R.style.DatePicker)
            val picker = builder.build().apply {
                addOnPositiveButtonClickListener { addEditViewModel.onDateSelected(it) }
            }
            picker.show(requireActivity().supportFragmentManager, picker.toString())
        })
    }
}
