package com.manuel.delivery.fragments.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.manuel.delivery.R
import com.manuel.delivery.adapters.CategoriesAdapter
import com.manuel.delivery.databinding.FragmentClientCategoriesBinding
import com.manuel.delivery.models.Category
import com.manuel.delivery.models.User
import com.manuel.delivery.providers.CategoriesProvider
import com.manuel.delivery.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientCategoriesFragment : Fragment() {
    private lateinit var categoriesProvider: CategoriesProvider
    private lateinit var categoriesAdapter: CategoriesAdapter
    private var binding: FragmentClientCategoriesBinding? = null
    private var user: User? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentClientCategoriesBinding.inflate(inflater, container, false)
        binding?.let { view ->
            user = Constants.getUserInSession(requireContext())
            view.toolbar.title = getString(R.string.categories)
            view.toolbar.setTitleTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorOnPrimary
                )
            )
            (activity as? AppCompatActivity)?.setSupportActionBar(view.toolbar)
            return view.root
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.let { b ->
            user?.let { u ->
                categoriesProvider = CategoriesProvider(u.sessionToken)
                categoriesProvider.getAll()?.enqueue(object : Callback<MutableList<Category>> {
                    override fun onResponse(
                        call: Call<MutableList<Category>>,
                        response: Response<MutableList<Category>>
                    ) {
                        response.body()?.let { listOfCategories ->
                            categoriesAdapter =
                                CategoriesAdapter(requireContext(), listOfCategories)
                            b.rvCategories.apply {
                                layoutManager = LinearLayoutManager(requireContext())
                                adapter = this@ClientCategoriesFragment.categoriesAdapter
                                setHasFixedSize(true)
                            }
                        }
                    }

                    override fun onFailure(call: Call<MutableList<Category>>, t: Throwable) {
                        Snackbar.make(
                            b.root,
                            getString(R.string.failed_to_get_all_categories),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}