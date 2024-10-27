package com.example.optionsmenupractice.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.optionsmenupractice.R
import saved_instance_data.SharedViewModel

class Dashboard : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val search: CardView = view.findViewById(R.id.search)
        val popular: CardView = view.findViewById(R.id.popular)
        val profile: CardView = view.findViewById(R.id.profile)
        val tickets: CardView = view.findViewById(R.id.tickets)
        val manageEvent: CardView = view.findViewById(R.id.manageevent)
        val myEvent: CardView = view.findViewById(R.id.myevents)

        // Get user_id from arguments safely
        val userId = arguments?.getString("user_id")?.toIntOrNull()

        // Update the shared ViewModel with user ID
        userId?.let {
            sharedViewModel.userId.value = it
        }

        // Bundle to pass the user ID to other fragments
        val bundleForward = Bundle().apply {
            userId?.let { putInt("user_id", it) }
            sharedViewModel.userId.value?.let { putInt("user_id", it) }
        }

        // Set up click listeners for navigation
        search.setOnClickListener { navigateToFragment(HomeFragment(), bundleForward) }
        popular.setOnClickListener { navigateToFragment(PopularFragment(), bundleForward) }
        profile.setOnClickListener { navigateToFragment(ProfileFragment(), bundleForward) }
        tickets.setOnClickListener { navigateToFragment(Tickets(), bundleForward) }
        manageEvent.setOnClickListener { navigateToFragment(ManageEvents(), bundleForward) }
        myEvent.setOnClickListener { navigateToFragment(MyEvents(), bundleForward) }

        return view
    }

    private fun navigateToFragment(fragment: Fragment, bundle: Bundle) {
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
