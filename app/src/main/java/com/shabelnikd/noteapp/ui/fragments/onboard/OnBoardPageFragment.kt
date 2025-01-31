package com.shabelnikd.noteapp.ui.fragments.onboard

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieDrawable
import com.shabelnikd.noteapp.R
import com.shabelnikd.noteapp.databinding.FragmentOnBoardPageBinding
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class OnBoardPageFragment : Fragment() {

    private var _binding: FragmentOnBoardPageBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnBoardPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        changeDotsIndicatorY()

    }

    private fun initialize() = with(binding) {
        when (arguments?.getInt(ARG_ON_BOARD_POSITION)) {
            0 -> {
                tvOnBoard.text = "Удобство"
                tvOnBoardDetail.text =
                    "Создавайте заметки в два клика! Записывайте мысли, идеи и важные задачи мгновенно."
                lottieOnBoard.setAnimation(R.raw.note_list)
            }

            1 -> {
                tvOnBoard.text = "Организация"
                tvOnBoardDetail.text =
                    "Организуйте заметки по папкам и тегам. Легко находите нужную информацию в любое время."
                lottieOnBoard.setAnimation(R.raw.organize_folder)
            }

            2 -> {
                tvOnBoard.text = "Синхронизация"
                tvOnBoardDetail.text =
                    "Синхронизация на всех устройствах. Доступ к записям в любое время и в любом месте."
                lottieOnBoard.setAnimation(R.raw.backup)
                lottieOnBoard.repeatMode = LottieDrawable.RESTART
            }
        }
    }

    private fun changeDotsIndicatorY() = with(binding.tvOnBoardDetail) {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)

                val pxToDp = fun(px: Int): Int {
                    return TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        px.toFloat(),
                        resources.displayMetrics
                    ).toInt()
                }

                val location = IntArray(2)
                binding.tvOnBoardDetail.getLocationOnScreen(location)

                val y = location[1] + binding.tvOnBoardDetail.height + pxToDp(40)

                val dotsIndicatorNewY = y
                val dotsIndicator =
                    requireActivity().findViewById<DotsIndicator>(R.id.dotsIndicator)
                dotsIndicator.y = dotsIndicatorNewY.toFloat()


            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_ON_BOARD_POSITION = "onBoard"
    }
}