package com.rebeccablum.naggle.ui

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.rebeccablum.naggle.R
import com.rebeccablum.naggle.databinding.FragmentAddEditNagDialogBinding
import com.rebeccablum.naggle.models.Priority
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AddEditNagFragment : Fragment() {

    private lateinit var binding: FragmentAddEditNagDialogBinding
    private lateinit var adapter: ArrayAdapter<Priority>

    private val addEditViewModel: AddEditNagViewModel by viewModel()
    private val args: AddEditNagFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddEditNagDialogBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = addEditViewModel
        adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            Priority.values()
        )
        binding.priorityDropdownMenu.setAdapter(adapter)

        addEditViewModel.fillValues(args.nagId)

        addEditViewModel.actionDismiss.observe(this, {
            findNavController().popBackStack()
        })
        addEditViewModel.errorDisplay.observe(this, {
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

        addEditViewModel.actionEditTime.observe(this, Observer {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                addEditViewModel.onTimeSelected(hour, minute)
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false
            ).show()
        })
    }
}
