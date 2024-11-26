package com.example.playlistmaker.media.ui.newPlaylist

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.media.domain.models.Playlist
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream


class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val newPlaylistViewModel: NewPlaylistViewModel by viewModel()

    private lateinit var pickImageLauncher: ActivityResultLauncher<PickVisualMediaRequest>

    private lateinit var confirmDialog: AlertDialog

    private var coverUri: Uri? = null

    private val args by navArgs<NewPlaylistFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editablePlaylist = args.playlist
        if (editablePlaylist == null) {
            binding.toolbarNewPlaylist.title = getString(R.string.new_playlist)
            binding.buttonCreate.text = getString(R.string.button_to_create)
            binding.buttonCreate.isEnabled = false
        } else {
            binding.toolbarNewPlaylist.title = getString(R.string.edit_playlist)
            binding.buttonCreate.text = getString(R.string.save)
            binding.textInputEditTextName.setText(editablePlaylist.name)
            binding.textInputEditTextDescription.setText(editablePlaylist.description)
            binding.buttonCreate.isEnabled = true

            editablePlaylist.coverPath?.let { uri ->
                setImageIntoView(uri.toUri())
                coverUri = uri.toUri()
            }
        }


        binding.toolbarNewPlaylist.setNavigationOnClickListener {
            if (editablePlaylist == null) {
                if (coverUri != null &&
                    binding.textInputEditTextName.text.toString().isNotBlank() &&
                    binding.textInputEditTextDescription.text.toString().isNotBlank()
                ) {
                    confirmDialog.show()
                } else {

                    findNavController().navigateUp()
                }
            } else {


                findNavController().navigateUp()
            }
        }


        binding.textInputEditTextName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.buttonCreate.isEnabled = s?.isNotBlank() ?: false
            }
        })


        binding.buttonCreate.setOnClickListener {
            val name = binding.textInputEditTextName.text.toString()
            val description = binding.textInputEditTextDescription.text.toString()

            newPlaylistViewModel.setPlaylistName(name)
            newPlaylistViewModel.setPlaylistDescription(description)

            coverUri?.let { uri ->
                val privateStorageUri = saveImageToPrivateStorage(uri)
                newPlaylistViewModel.setCoverImageUri(privateStorageUri)
            }

            if (editablePlaylist == null) {
                saveNewPlaylist(name, description)
            } else {
                editExistingPlaylist(editablePlaylist, name, description)
            }

            findNavController().navigateUp()
        }



        newPlaylistViewModel.savePlaylistResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = {
                    Toast.makeText(requireContext(), "Плейлист создан!", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()
                },
                onFailure = { exception ->
                    val errorMessage = exception.message ?: "Неизвестная ошибка"
                    Toast.makeText(requireContext(), "Ошибка: $errorMessage", Toast.LENGTH_LONG)
                        .show()
                }
            )
        }

        newPlaylistViewModel.playlistName.observe(viewLifecycleOwner) { name ->
            binding.textInputEditTextName.setText(name)
        }

        newPlaylistViewModel.playlistDescription.observe(viewLifecycleOwner) { description ->
            binding.textInputEditTextDescription.setText(description)
        }


        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    coverUri = uri
                    setImageIntoView(uri)
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
                    if (
                        coverUri != null
                        || binding.textInputEditTextName.text.toString().isNotBlank()
                        || binding.textInputEditTextDescription.text.toString().isNotBlank()
                    ) {
                        confirmDialog.show()
                    } else {
                        findNavController().navigateUp()
                    }
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

    private fun saveNewPlaylist(name: String, description: String) {
        val newPlaylist = Playlist(
            0,
            name,
            description,
            coverUri?.toString(),
            emptyList(),
            0
        )

        newPlaylistViewModel.savePlaylist(newPlaylist)
    }

    private fun editExistingPlaylist(playlist: Playlist, name: String, description: String) {
        val updatedPlaylist =
            playlist.copy(name = name, description = description, coverPath = coverUri?.toString())

        newPlaylistViewModel.saveEditPlaylist(updatedPlaylist)
    }

    private fun setImageIntoView(uri: Uri) {
        Glide.with(requireContext())
            .load(uri)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(requireContext().resources.getDimensionPixelSize(R.dimen.playlistCover_radius)))
            .into(binding.placeholderNewPlaylist)
    }

    private fun saveImageToPrivateStorage(uri: Uri): Uri {
        val filePath = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlistsImages"
        )

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val file = File(filePath, uri.toString().substringAfterLast("/"))

        val inputStream = requireActivity().contentResolver.openInputStream(uri)

        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        return file.toUri()
    }

}




