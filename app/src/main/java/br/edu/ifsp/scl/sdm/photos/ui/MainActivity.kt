package br.edu.ifsp.scl.sdm.photos.ui

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Toast
import br.edu.ifsp.scl.sdm.photos.R
import br.edu.ifsp.scl.sdm.photos.adapter.ProductAdapter
import br.edu.ifsp.scl.sdm.photos.databinding.ActivityMainBinding
import br.edu.ifsp.scl.sdm.photos.model.JsonPlaceHolder
import br.edu.ifsp.scl.sdm.photos.model.Pruduct
import com.android.volley.toolbox.ImageRequest

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val productList: MutableList<Pruduct> = mutableListOf()
    private val productAdapter: ProductAdapter by lazy {
        ProductAdapter(this, productList)
    }

    companion object {
        const val PRODUCTS_ENDPOINT = "https://jsonplaceholder.typicode.com/photos"
      }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        setSupportActionBar(amb.mainTb.apply {
            title = getString(R.string.app_name)
        })

        amb.productsSp.apply {
            adapter = productAdapter
            onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    amb.imageView.setImageBitmap(null)
                    amb.imageView2.setImageBitmap(null)
                    retrieveProductImage(productList[position], true)
                    retrieveProductImage(productList[position], false)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // NSA
                }
            }
        }

        retrieveProducts()
    }

    private fun retrieveProducts() = JsonPlaceHolder.ProductListRequest(
        { pruducts ->
            pruducts.also { productAdapter.addAll(it) }
        },
        {
            Toast.makeText(this, getString(R.string.request_problem), Toast.LENGTH_SHORT).show()
        }
    ).also {
        JsonPlaceHolder.getInstance(this).addToRequestQueue(it)
    }

    private fun retrieveProductImage(pruduct: Pruduct, isMainImage: Boolean) = ImageRequest(
        getValidImageUrl(if(isMainImage) pruduct.url else pruduct.thumbnailUrl, pruduct.title),
        { response ->
            if(isMainImage){
                amb.imageView.setImageBitmap(response)
            }else{
                amb.imageView2.setImageBitmap(response)
            }
        },
        0,
        0,
        ImageView.ScaleType.CENTER,
        Bitmap.Config.ARGB_8888,
        {
            println(it?.message)
            Toast.makeText(this, getString(R.string.request_problem), Toast.LENGTH_SHORT).show()
        }
    ).also {
        JsonPlaceHolder.getInstance(this).addToRequestQueue(it)
    }

    private fun getValidImageUrl(imageUrl: String, imageTitle: String): String {
        if(!imageUrl.contains("https://via.placeholder.com/")) return imageUrl

        val urlPaths = imageUrl.replace("https://via.placeholder.com/", "")
        val (size, color) = urlPaths.split("/")
        return "https://placehold.co/$size" + "x$size/$color/white/jpg?text=$imageTitle"
    }
}