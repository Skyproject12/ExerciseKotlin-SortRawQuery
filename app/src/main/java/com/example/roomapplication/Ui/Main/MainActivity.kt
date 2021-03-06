package com.example.roomapplication.Ui.Main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomapplication.Data.Source.Model.Entity.Note
import com.example.roomapplication.Data.Source.Model.SortUtils
import com.example.roomapplication.R
import com.example.roomapplication.Ui.Insert.InsertActivity
import com.example.roomapplication.ViewModel.Main.MainViewModel
import com.example.roomapplication.ViewModel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

// catatan menagpa digunakan activityfor result karena agar selesai di insert result (toast) akan di tampilkan di activity main bukan di activity insert
class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: MainListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // initial view model
        mainViewModel = obtainViewModel(this@MainActivity)
        // set data use view model
        mainViewModel.getAllNotes(SortUtils.NEWEST)
            .observe(this, Observer<PagedList<Note>> { noteList ->
                if (noteList != null) {
                    // select from database , set adapter with data android
                    adapter.submitList(noteList)
                }
            })
        // set adapter
        adapter = MainListAdapter(this@MainActivity)
        rv_notes.layoutManager = LinearLayoutManager(this)
        rv_notes.setHasFixedSize(true)
        rv_notes.adapter = adapter
        // set onclick the fab
        fab_add.setOnClickListener { view ->
            if (view.id == R.id.fab_add) {
                // intent to insert activity
                val intent = Intent(this@MainActivity, InsertActivity::class.java)
                // start activity with result Request add
                startActivityForResult(intent, InsertActivity.REQUEST_ADD)
            }
        }
    }

    // initial of the view model
    private fun obtainViewModel(activity: AppCompatActivity): MainViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        // set view model provider
        return ViewModelProvider(activity, factory).get(MainViewModel::class.java)

    }

    private val noteObserver = Observer<PagedList<Note>> { noteList ->
        if (noteList != null) {
            // select from database , set adapter with thE data android
            adapter.submitList(noteList)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            // ketika request code adalah insert
            if (requestCode == InsertActivity.REQUEST_ADD) {
                if (resultCode == InsertActivity.RESULT_ADD) {
                    showSnackBarMessage(getString(R.string.added))
                }
            }
            // ketika request code adalah update
            else if (requestCode == InsertActivity.REQUEST_UPDATE) {
                if (resultCode == InsertActivity.RESULT_UPDATE) {
                    showSnackBarMessage(getString(R.string.update))
                } else if (resultCode == InsertActivity.RESULT_DELETE) {
                    showSnackBarMessage(getString(R.string.delete))
                }
            }
        }
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(rv_notes, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var sort = ""
        // ketika menu yang dipilih adalah
        when (item.itemId) {
            // ketika menu baru
            R.id.action_new -> sort = SortUtils.NEWEST
            // ketika menu lama
            R.id.action_old -> sort = SortUtils.OLDEST
        }
        // tampilkan soeting berdasarkan menu yang di klik
        mainViewModel.getAllNotes(sort).observe(this, noteObserver)
        // set cheked pada menu true
        item.setChecked(true)
        return super.onOptionsItemSelected(item)
    }
}
