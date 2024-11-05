package com.example.playlistmaker.media.ui.newPlaylist


import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val newPlaylistViewModel: NewPlaylistViewModel by viewModel()

    private lateinit var pickImageLauncher: ActivityResultLauncher<PickVisualMediaRequest>


    private lateinit var confirmDialog: AlertDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCreate.isEnabled = false // Кнопка "Создать" изначально неактивна

        binding.toolbarNewPlaylist.setOnClickListener {
            confirmDialog.show()
        }

        // Слушатель изменений текста в EditText для названия
        binding.textInputEditTextName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.buttonCreate.isEnabled = s?.isNotEmpty() ?: false
                hideKeyboard()
            }
        })

        // Слушатель изменений текста в EditText для названия
        binding.textInputEditTextDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                //binding.buttonCreate.isEnabled = s?.isNotEmpty() ?: false
                hideKeyboard()
            }
        })

        binding.textInputEditTextName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }
        binding.textInputEditTextDescription.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }


        binding.buttonCreate.setOnClickListener {
            val name = binding.textInputEditTextName.text.toString()
            val description = binding.textInputEditTextDescription.text.toString()
            newPlaylistViewModel.setPlaylistName(name)
            newPlaylistViewModel.setPlaylistDescription(description)
            newPlaylistViewModel.savePlaylist()
        }

        newPlaylistViewModel.savePlaylistResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = {
                    // Действия при успехе
                    Toast.makeText(requireContext(), "Плейлист создан!", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()
                },
                onFailure = { exception ->
                    // Действия при ошибке
                    val errorMessage = exception.message ?: "Неизвестная ошибка"
                    Toast.makeText(requireContext(), "Ошибка: $errorMessage", Toast.LENGTH_LONG)
                        .show()
                    Log.e("PlaylistCreation", "Error saving playlist", exception)
                }
            )
        }



        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    newPlaylistViewModel.setCoverImageUri(uri)
                    binding.placeholderNewPlaylist.setImageURI(uri)
                } else {
                    Toast.makeText(requireContext(), "Изображение не выбрано", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        binding.placeholderNewPlaylist.setOnClickListener {
            pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }



        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    confirmDialog.show()
                }
            })

        confirmDialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNegativeButton("Отмена") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("Завершить") { dialog, which ->
                requireActivity().supportFragmentManager.popBackStack()
            }
            .create()

    }

    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}

