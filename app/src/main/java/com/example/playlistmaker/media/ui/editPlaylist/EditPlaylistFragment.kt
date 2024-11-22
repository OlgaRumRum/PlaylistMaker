package com.example.playlistmaker.media.ui.editPlaylist

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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class EditPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val editPlaylistViewModel: EditPlaylistViewModel by viewModel()

    private lateinit var pickImageLauncher: ActivityResultLauncher<PickVisualMediaRequest>

    private var coverUri: Uri? = null

    private val args by navArgs<EditPlaylistFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCreate.isEnabled = false

        binding.textInputEditTextName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.buttonCreate.isEnabled = s?.isNotEmpty() ?: false
            }
        })

        binding.toolbarNewPlaylist.title = getString(R.string.edit_playlist)

        binding.toolbarNewPlaylist.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        initializeViews()

        binding.buttonCreate.text = getString(R.string.save)

        binding.buttonCreate.setOnClickListener {

            val updatedPlaylist = if (coverUri == null) {
                args.playlist.copy(
                    name = binding.textInputEditTextName.text.toString(),
                    description = binding.textInputEditTextDescription.text.toString()
                )
            } else {
                args.playlist.copy(
                    name = binding.textInputEditTextName.text.toString(),
                    description = binding.textInputEditTextDescription.text.toString(),
                    coverPath = saveImageToPrivateStorage(coverUri!!).toString()
                )
            }

            editPlaylistViewModel.savePlaylist(updatedPlaylist)

            findNavController().navigateUp()

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

        editPlaylistViewModel.playlistName.observe(viewLifecycleOwner) { name ->
            binding.textInputEditTextName.setText(name)
        }

        editPlaylistViewModel.playlistDescription.observe(viewLifecycleOwner) { description ->
            binding.textInputEditTextDescription.setText(description)
        }

        editPlaylistViewModel.coverImageUri.observe(viewLifecycleOwner) { uri ->
            coverUri = uri
            setImageIntoView(uri)
        }
    }

    private fun setImageIntoView(uri: Uri?) {
        if (uri != null) {
            Glide.with(requireContext())
                .load(uri)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(binding.placeholderNewPlaylist)
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri): Uri {
        val filePath = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlistsImages"
        )

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val fileName =
            uri.lastPathSegment?.substringAfterLast("/")?.let { "playlist_$it" } ?: "default.jpg"
        val file = File(filePath, fileName)

        requireActivity().contentResolver.openInputStream(uri).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                BitmapFactory.decodeStream(inputStream)
                    .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
            }
        }

        return file.toUri()
    }

    private fun initializeViews() {
        binding.textInputEditTextName.setText(args.playlist.name)
        binding.textInputEditTextDescription.setText(args.playlist.description)
        if (args.playlist.coverPath != null) {
            binding.placeholderNewPlaylist.setImageURI(Uri.parse(args.playlist.coverPath))
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

