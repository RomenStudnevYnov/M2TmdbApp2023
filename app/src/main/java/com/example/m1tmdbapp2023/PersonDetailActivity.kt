package com.example.m1tmdbapp2023

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.commitNow
import com.example.m1tmdbapp2023.databinding.ActivityPersonDetailBinding
import com.example.m1tmdbapp2023.model.PersonDetails
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val PERSON_ID_EXTRA_KEY = "person_id_ek"
class PersonDetailActivity : AppCompatActivity() {

    private val LOGTAG = MainActivity::class.simpleName

    lateinit var binding: ActivityPersonDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityPersonDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tmdbapi = ApiClient.instance.create(ITmdbApi::class.java)

        val personId = intent.extras?.getString(PERSON_ID_EXTRA_KEY)!!.toInt()
        val call = tmdbapi.getPersonDetails(personId, TMDB_API_KEY)
        call.enqueue(object : Callback<PersonDetails> {
            override fun onResponse(
                call: Call<PersonDetails>,
                response: Response<PersonDetails>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val person = response.body()!!
                    binding.nameTv.text = person.name
                    binding.birthDateTv.text = person.birthday
                    binding.birthPlaceTv.text = person.placeOfBirth
                    Picasso.get()
                        .load(ApiClient.IMAGE_BASE_URL + person.profilePath)
                        .placeholder(android.R.drawable.progress_horizontal)
                        .error(android.R.drawable.stat_notify_error)
                        .into(binding.photoIv)
                    binding.biographyTv.text = person.biography

                    // Social bar setup
                    binding.socialBarFcv.tag = personId.toString()
                    val bundle = bundleOf(
                        "sbfc_view_tag" to binding.socialBarFcv.tag
                    )
                    supportFragmentManager.commitNow {
                        add(binding.socialBarFcv.id, SocialBarFragment::class.java, bundle )
                    }
                } else {
                    Log.e(LOGTAG, "Call to getPersonDetails failed with error ${response.code()}")
                    displayFailureToast()
                }
            }

            override fun onFailure(call: Call<PersonDetails>, t: Throwable) {
                Log.e(LOGTAG,"Call to getPersonDetails catastrophically failed")
                displayFailureToast()
            }
        })

    }

    fun displayFailureToast() {
        Toast.makeText(applicationContext, "Failed to load person details", Toast.LENGTH_SHORT).show()
    }
}