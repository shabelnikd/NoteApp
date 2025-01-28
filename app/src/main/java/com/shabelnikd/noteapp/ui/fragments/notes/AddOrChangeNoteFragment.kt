package com.shabelnikd.noteapp.ui.fragments.notes

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shabelnikd.noteapp.Dependencies
import com.shabelnikd.noteapp.database.tuples.NoteTuple
import com.shabelnikd.noteapp.databinding.FragmentAddOrChangeNoteBinding
import com.shabelnikd.noteapp.databinding.NoteContextMenuBinding
import com.shabelnikd.noteapp.models.ColorsEnum
import com.shabelnikd.noteapp.models.Note
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddOrChangeNoteFragment : Fragment() {

    private var _binding: FragmentAddOrChangeNoteBinding? = null
    private val binding get() = _binding!!

    private var _bindingPopup: NoteContextMenuBinding? = null
    private val bindingPopup get() = _bindingPopup!!

    private val args: AddOrChangeNoteFragmentArgs by navArgs()
    private val viewModel: AddOrChangeViewModel by viewModels()

    private var etTitleFilled = false
    private var etTextFilled = false

    private var ongoingPopUp: PopupWindow? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddOrChangeNoteBinding.inflate(inflater, container, false)
        _bindingPopup = NoteContextMenuBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSaveNote.visibility = View.GONE
        binding.btnNoteContext.isActivated = false

        initialize()
        setTextChangedListeners()
        setupPopUp()
    }

    private fun initialize() {
        binding.btnNewNoteCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnNoteContext.setOnClickListener { view ->
            showPopup(view)
        }


        viewModel.currentFolderId.observe(viewLifecycleOwner) { folderId ->
            val currentDate = getCurrentDateTime().substringBeforeLast('|')
            val currentTime = getCurrentDateTime().substringAfterLast('|')

            when (args.noteId) {
                -1L -> { // -1 is new Note
                    binding.tvNoteDateDay.text = currentDate
                    binding.tvNoteDateTime.text = currentTime

                    binding.btnSaveNote.setOnClickListener {
                        viewModel.insertNote(
                            Note(
                                title = binding.etNoteTitle.text.toString(),
                                text = binding.etTextNote.text.toString(),
                                colorHex = searchSelected(),
                                folderId = if (folderId == -1L) null else folderId,
                                createdAt = getCurrentDateTime()
                            ).toNoteEntity()
                        )
                        findNavController().popBackStack()
                    }
                }

                else -> { //Else for noteId
                    lifecycleScope.launch {
                        with(Dependencies.noteRepository) {
                            getNoteById(args.noteId).observe(viewLifecycleOwner) { note ->
                                selectColor(note.colorHex)
                                bindingPopup.btnResetColor.setOnClickListener {
                                    viewModel.changeColor(note.id, ColorsEnum.DEFAULT.colorHex)
                                    selectColor(ColorsEnum.DEFAULT.colorHex, false)
                                }

                                with(binding) {
                                    etNoteTitle.setText(note.title)
                                    etTextNote.setText(note.text)

                                    tvNoteDateDay.text = note.createdAt.substringBeforeLast('|')
                                    tvNoteDateTime.text = note.createdAt.substringAfterLast('|')

                                    btnSaveNote.setOnClickListener {
                                        val changedNote = NoteTuple(
                                            id = note.id,
                                            folderId = if (folderId == -1L) null else folderId,
                                            title = etNoteTitle.text.toString(),
                                            text = etTextNote.text.toString(),
                                            colorHex = searchSelected(),
                                            createdAt = note.createdAt,
                                            isNoteDeleted = false
                                        )

                                        viewModel.updateNote(note, changedNote)
                                        findNavController().popBackStack()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setTextChangedListeners() {
        val etTitleStateChange = fun(state: Boolean) { etTitleFilled = state }
        val etTextStateChange = fun(state: Boolean) { etTextFilled = state }

        binding.etNoteTitle.addTextChangedListener(returnTextWatcher(etTitleStateChange))
        binding.etTextNote.addTextChangedListener(returnTextWatcher(etTextStateChange))

    }


    fun returnTextWatcher(changeState: (state: Boolean) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                changeState(!s.isNullOrEmpty())
                if (etTextFilled && etTitleFilled) {
                    binding.btnSaveNote.visibility = View.VISIBLE
                    binding.btnNoteContext.isActivated = true
                } else {
                    binding.btnSaveNote.visibility = View.GONE
                    binding.btnNoteContext.isActivated = false
                }
            }
        }
    }

    private fun listBtnColor() = with(bindingPopup) {
        return@with listOf(
            setYellow, setPurple, setPink,
            setRed, setGreen, setBlue
        )
    }

    private fun setupPopUp() = with(bindingPopup) {
        listBtnColor().forEach { btn ->
            btn.setOnClickListener {
                listBtnColor().forEach { otherButton ->
                    otherButton.isSelected = false
                }
                btn.isSelected = true
            }
        }
    }

    private fun selectColor(noteColor: String, state: Boolean = true) = with(bindingPopup) {
        when (state) {
            true -> {
                listBtnColor().filter {
                    (0xFFFFFF and (it.cardBackgroundColor.defaultColor)) == 0xFFFFFF and (Color.parseColor(
                        noteColor
                    ))
                }.firstOrNull()?.isSelected = true
            }

            false -> {
                listBtnColor().forEach {
                    it.isSelected = false
                }
            }
        }


    }

    private fun searchSelected() = with(bindingPopup) {
        val color =
            listBtnColor().filter { it.isSelected }.firstOrNull()?.cardBackgroundColor?.defaultColor
                ?: Color.parseColor(ColorsEnum.DEFAULT.colorHex)

        return@with String.format("#%06X", (0xFFFFFF and color))
    }

    private fun showPopup(view: View) {
        val widthInPx = 152
        val heightInPx = 145
        val marginInPx = 10

        val pxToDp = fun(px: Int): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                px.toFloat(),
                resources.displayMetrics
            ).toInt()
        }

        ongoingPopUp = PopupWindow(
            bindingPopup.root,
            pxToDp(widthInPx),
            pxToDp(heightInPx),
            true
        )

        val viewWidth = view.width
        val offsetX = viewWidth - pxToDp(widthInPx) - pxToDp(marginInPx)

        ongoingPopUp?.showAsDropDown(view, offsetX, 0)
    }


    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("dd MMMM|HH:mm", Locale("ru"))
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        ongoingPopUp = null
        _bindingPopup = null
    }

}