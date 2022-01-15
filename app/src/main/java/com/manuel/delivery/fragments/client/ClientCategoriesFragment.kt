package com.manuel.delivery.fragments.client

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.manuel.delivery.R
import com.manuel.delivery.activities.client.products.my_list.ClientProductsMyListActivity
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
        binding?.let { b ->
            setupToolbar()
            setHasOptionsMenu(true)
            user = Constants.getUserInSession(requireContext())
            return b.root
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun setupToolbar() {
        binding?.let { b ->
            b.toolbar.title = getString(R.string.categories)
            b.toolbar.setTitleTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorOnPrimary
                )
            )
            (activity as? AppCompatActivity)?.setSupportActionBar(b.toolbar)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCategories()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_list_of_products, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_my_product_list) {
            startActivity(Intent(requireContext(), ClientProductsMyListActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getCategories() {
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
                            t.message.toString(),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
    }
}