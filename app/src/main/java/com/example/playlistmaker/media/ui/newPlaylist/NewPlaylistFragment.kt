package com.example.playlistmaker.media.ui.newPlaylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding


class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return inflater.inflate(R.layout.fragment_new_playlist, container, false)
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*binding.textInputEditTextName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            binding.textInputLayoutName.style = if (hasFocus) R.style.TextInputLayoutFocused else R.style.TextInputLayoutUnfocused
        }

         */
    }

    companion object {

    }
}