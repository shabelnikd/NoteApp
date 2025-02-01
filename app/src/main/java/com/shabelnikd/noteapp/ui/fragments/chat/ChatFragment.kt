package com.shabelnikd.noteapp.ui.fragments.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.getField
import com.shabelnikd.noteapp.adapters.ChatAdapter
import com.shabelnikd.noteapp.databinding.FragmentChatBinding
import com.shabelnikd.noteapp.models.Message

class ChatFragment : Fragment() {

    private val viewModel: ChatViewModel by viewModels()

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val db = Firebase.firestore
    private val query = db.collection("chat").orderBy("timestamp")

    private var _listener: ListenerRegistration? = null
    private val listener get() = _listener!!

    private val chatAdapter = ChatAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSend.isEnabled = false
        setupListeners()
        binding.rvChat.adapter = chatAdapter
        dataObserver()
    }

    private fun setupListeners() {
        binding.btnSend.setOnClickListener {
            val message = hashMapOf(
                "message" to binding.etMessage.text.toString(),
                "timestamp" to FieldValue.serverTimestamp()
            )
            db.collection("chat")
                .add(message)
                .addOnSuccessListener {
                    binding.etMessage.setText("")
                    binding.rvChat.smoothScrollToPosition(chatAdapter.itemCount - 1)
                }
                .addOnFailureListener {
                    Snackbar.make(binding.root, "Не отправлено", 1000).show()
                }
        }

        binding.etMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                binding.btnSend.isEnabled = !p0.isNullOrEmpty()
            }

        })
    }

    private fun dataObserver() {
        _listener = query.addSnapshotListener { value, error ->
            if (error != null) return@addSnapshotListener

            value?.let { snapshot ->

                val messages = snapshot.documents.map { doc ->
                    doc.getString("message")?.let { msg ->
                        Message(
                            msg,
                            doc.getField("timestamp"),
                            doc.id
                        )
                    }

                }
                chatAdapter.submitList(messages)
            }
        }
    }

    private fun deleteDocById(docId: String) {
        db.collection("chat").document(docId).delete()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}