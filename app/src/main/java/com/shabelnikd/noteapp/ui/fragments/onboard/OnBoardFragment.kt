package com.shabelnikd.noteapp.ui.fragments.onboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shabelnikd.noteapp.R
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.shabelnikd.noteapp.adapters.OnBoardAdapter
import com.shabelnikd.noteapp.databinding.FragmentOnBoardBinding
import com.shabelnikd.noteapp.datastore.DataStoreManager
import kotlinx.coroutines.launch

class OnBoardFragment : Fragment() {

    private var _binding: FragmentOnBoardBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization()
        setupListeners()
    }

    private fun initialization() {
        binding.onBoardViewPager.adapter = OnBoardAdapter(this)
        binding.dotsIndicator.attachTo(binding.onBoardViewPager)

    }

    private fun setupListeners() = with(binding.onBoardViewPager) {
        registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int): Unit = with(binding) {
                super.onPageSelected(position)
                if (position == adapter?.itemCount?.minus(1)) {
                    tvClickableSkip.animate()
                        .alpha(0f)
                        .translationY(-tvClickableSkip.height.toFloat())
                        .translationX(tvClickableSkip.right.toFloat())
                        .setDuration(300)
                        .setInterpolator(AnticipateOvershootInterpolator())
                        .withEndAction {
                            tvClickableSkip.visibility = View.INVISIBLE
                        }

                    btnStart.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(300)
                        .setInterpolator(OvershootInterpolator())
                        .withStartAction {
                            btnStart.visibility = View.VISIBLE
                        }

                } else {
                    tvClickableSkip.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .translationX(0f)
                        .setDuration(300)
                        .setInterpolator(OvershootInterpolator())
                        .withStartAction {
                            tvClickableSkip.visibility = View.VISIBLE
                        }

                    btnStart.animate()
                        .alpha(0f)
                        .translationY(btnStart.height.toFloat())
                        .setDuration(300)
                        .setInterpolator(OvershootInterpolator())
                        .withEndAction {
                            btnStart.visibility = View.INVISIBLE
                        }

                    tvClickableSkip.setOnClickListener {
                        currentItem = adapter?.itemCount!!
                    }
                }
            }
        })

        binding.btnStart.setOnClickListener {
            findNavController().navigate(R.id.action_onBoardFragment_to_firebaseUIActivity)
            lifecycleScope.launch {
                DataStoreManager.setFirstLaunch(false)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}