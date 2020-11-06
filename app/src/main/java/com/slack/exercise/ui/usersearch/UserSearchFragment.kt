package com.slack.exercise.ui.usersearch

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.slack.exercise.FileManager
import com.slack.exercise.R
import com.slack.exercise.databinding.FragmentUserSearchBinding
import com.slack.exercise.model.UserSearchResult
import dagger.android.support.DaggerFragment
import timber.log.Timber
import java.io.*
import javax.inject.Inject


/**
 * Main fragment displaying and handling interactions with the view.
 * We use the MVP pattern and attach a Presenter that will be in charge of non view related operations.
 */
class UserSearchFragment : DaggerFragment(), UserSearchContract.View {

  @Inject
  internal lateinit var presenter: UserSearchPresenter

  private lateinit var userSearchBinding: FragmentUserSearchBinding

  var currentSearchTerm = ""

  private val fileManager = FileManager()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    super.onCreateView(inflater, container, savedInstanceState)
    userSearchBinding = FragmentUserSearchBinding.inflate(inflater, container, false)
    setHasOptionsMenu(true)
    return userSearchBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUpToolbar()
    setUpList()
  }

  override fun onStart() {
    super.onStart()

    presenter.attach(this)
  }

  override fun onStop() {
    super.onStop()

    presenter.detach()
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_user_search, menu)

    val searchView: SearchView = menu.findItem(R.id.search_menu_item).actionView as SearchView
    searchView.queryHint = getString(R.string.search_users_hint)
    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query: String): Boolean {
        return true
      }

      override fun onQueryTextChange(newText: String): Boolean {
        val isDenied = findTextInDenyList(newText, R.raw.denylist)

        if (isDenied) {
          presentFailedSearchToast()
        } else {
          presenter.onQueryTextChange(newText)
          currentSearchTerm = newText
        }
        return true
      }
    })
  }

  fun findTextInDenyList(word: String, fileResource: Int): Boolean {
    var result = false
    val denyList = context?.let { fileManager.readFromFile(it, fileResource) }

    if (denyList != null) {
      for(deniedWord in denyList) {
        if (word == deniedWord) {
          Timber.i("search term, $word, found in denyList")
          result = true
          break
        }
      }
    }
    return result
  }

  private fun addItemToDenyList(word: String, fileName: String) {
    context?.let { fileManager.writeToFile(it, fileName, word) }

    Timber.i("$word: successfully saved to denyList")
  }

  fun presentFailedSearchToast() {
    Toast.makeText(context, getString(R.string.denied_search_term_message), Toast.LENGTH_SHORT).show()
  }


  override fun onUserSearchResults(results: Set<UserSearchResult>) {
    val adapter = userSearchBinding.userSearchResultList.adapter as UserSearchAdapter
    adapter.setResults(results)

    if (results.isEmpty()) {
      addItemToDenyList(currentSearchTerm, DENY_LIST)
    }
  }

  override fun onUserSearchError(error: Throwable) {
    Timber.e(error, "Error searching users.")
    Toast.makeText(context, getString(R.string.failed_search_message), Toast.LENGTH_SHORT).show()
  }

  private fun setUpToolbar() {
    val act = activity as UserSearchActivity
    act.setSupportActionBar(userSearchBinding.toolbar)
  }

  private fun setUpList() {
    with(userSearchBinding.userSearchResultList) {
      adapter = UserSearchAdapter()
      layoutManager = LinearLayoutManager(activity).apply {
        orientation = LinearLayoutManager.VERTICAL
      }
      setHasFixedSize(true)
    }
  }
  companion object {
    const val DENY_LIST = "denylist.txt"
  }
}