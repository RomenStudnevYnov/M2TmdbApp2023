package com.example.m1tmdbapp2023

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.m1tmdbapp2023.databinding.FragmentSocialBarBinding
import com.example.m1tmdbapp2023.db.SocialBarEntity

class SocialBarFragment : Fragment() {

    companion object {
        fun newInstance() = SocialBarFragment()
    }
    private val  LOGTAG = SocialBarFragment::class.simpleName
    private lateinit var binding : FragmentSocialBarBinding
    private var cn : Int? = Color.LTGRAY
    private var cs : Int? = Color.RED

    // Using the activityViewModels() Kotlin property delegate from the
    // fragment-ktx artifact to retrieve the ViewModel in the activity scope
    private val viewModel by activityViewModels<SocialBarViewModel> {
        SocialBarViewModelFactory(((requireContext() as Activity).application as TmdbApplication).socialBarDao)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSocialBarBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cn = getContext()?.getColor(R.color.ic_social_normal)
        cs = getContext()?.getColor(R.color.ic_social_selected)

        requireArguments().getString("sbfc_view_tag").let {
            val mapkey = it.toString().toInt()
            Log.d(LOGTAG,"mk=$mapkey")

            // set like button
            val nblikes = viewModel.nbLikes.value?.getOrElse(mapkey, {0}) ?: 0
            binding.nbLikeTv.text = nblikes.toString()
            val likeColor =  if (nblikes > 0 ) cs!! else cn!!
            binding.likeIv.setColorFilter(likeColor)
            binding.nbLikeTv.setTextColor(likeColor)

            binding.likeIv.setOnClickListener {
                var nbLikesNow = viewModel.nbLikes.value?.getOrElse(mapkey, {0}) ?: 0
                val isFavorite = viewModel.isFavorite.value?.getOrElse(mapkey,{false}) ?: false
                nbLikesNow++
                viewModel.nbLikes.value?.set(mapkey,nbLikesNow)
                viewModel.insert(SocialBarEntity(mapkey, isFavorite, nbLikesNow ))
                binding.nbLikeTv.setText(nbLikesNow.toString())
                binding.likeIv.setColorFilter(cs!!)
                binding.nbLikeTv.setTextColor(cs!!)
            }

            // set favorite button
            val isFavorite = viewModel.isFavorite.value?.getOrElse(mapkey,{false}) ?: false
            binding.favoriteIv.setColorFilter(if (isFavorite) cs!! else cn!!)
            binding.favoriteIv.setOnClickListener {
                var isFavoriteNow = viewModel.isFavorite.value?.getOrElse(mapkey,{false}) ?: false
                val nbLikes = viewModel.nbLikes.value?.getOrElse(mapkey, {0}) ?: 0
                isFavoriteNow = ! isFavoriteNow
                viewModel.isFavorite.value?.set(mapkey, isFavoriteNow)
                viewModel.insert(SocialBarEntity(mapkey,isFavoriteNow,nbLikes))
                binding.favoriteIv.setColorFilter(if (isFavoriteNow) cs!! else cn!! )
            }

            // set share button
            binding.shareIv.setColorFilter(cn!!)
            binding.shareIv.setOnClickListener {
                Log.d(LOGTAG,"shared clicked for id=${mapkey}")
            }
        }

    }

   /*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SocialBarViewModel::class.java)
        // TODO: Use the ViewModel
    }*/

}