# android-code-exercise

Thanks for the opportunity to work on this coding challenge! I had a great time working on it.

My approach:
First, I created a FileManager class to handle reading and writing to the denyList.txt file. I
wanted to pull that functionality out into it's own class with a single responsibility.

I am new the MVP architecture. In my current role, we use MVVM heavily and rely on LiveData a lot to
pass information around so figuring out where to call the FileManager in an MVP architecture was
tricky for me and something I would love to learn more about. In the end, because FileManager requires
access to Context, I called it from the UserSearchFragment, rather than the UserSearchPresenter, but
I would appreciate some feedback on a better way to have done that and make it all more testable.

With the goal of avoiding calling the search API if the search term was in the denyList, I updated the
onQueryTextChanged() callback to first check for the word in the denyList before calling the API. I also
added a Toast message so the user would know if they had tried an invalid search. I attempted to break
up the functionality of searching through the denyList, adding a word to the denyList, and presenting
the Toast message into separate smaller functions to increase readability. Any feedback you have about
how I could make this code cleaner, would be great to learn from.

Finally, as I was running my changes on my device, I tested it with Voice Assistant turned on and
noticed that it wasn't possible for the user to have the search results read to them, so I added a
contentDescription to each result in the RecyclerView to fix that. 
