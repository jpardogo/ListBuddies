ListBuddies
===========

Android library of a pair of auto-scroll circular parallax ListViews like the ones on the expedia app home page.

A video example of this library is on this [youtube video][1].

<p align="center">
 <img src="https://raw.github.com/jpardogo/ListBuddies/master/art/screenshot_listbuddies_1.png"/>
</p>

THIS PROJECT IS UNDER DEVELOPMENT. I would appreciate any kind of help. Thanks

Usage
-----

You must declare the following view in you on your xml layout:

```xml
<com.jpardogo.listbuddies.lib.views.ListBuddiesLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/listbuddies"
    android:layout_width="match_parent"
    android:drawSelectorOnTop="true"
    android:layout_height="match_parent"/>
```
This `LinerLayout` contains two ListViews. 
So we need to set the adapters of the ListViews calling `listBuddies.setAdapters(adapter1,adapter2)`. 
```java
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListBuddiesLayout listBuddies = (ListBuddiesLayout) rootView.findViewById(R.id.listbuddies);
        CircularAdapter adapter = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.image_size1), ImagesUrls.imageUrls_left);
        CircularAdapter adapter2 = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.image_size2), ImagesUrls.imageUrls_right);
        listBuddies.setAdapters(adapter, adapter2);
        return rootView;
    }
```

Both adapters need to be extend from `CircularLoopAdapter`. With minimal differences from a BaseAdapter.

```java
    public class CircularAdapter extends CircularLoopAdapter
```
The first different is that the adapter needs to `@Override getCircularCount` instead of `getCount`.

```java
    @Override
    protected int getCircularCount() {
        return mItems.size();
    }
```

and instead of get the value of `position` to get the item from the list. We need to get the position calling `getCircularPosition(position)`, like this:

```java
    @Override
    public String getItem(int position) {
        return mItems.get(getCircularPosition(position));
    }
```

To receive the callback for the click on the items of the lists, Just call `setOnItemClickListener` on your `ListBuddiesLayout` view and pass and instance of `OnBuddyItemClickListener`.

```java
public class ListBuddiesFragment extends Fragment implements ListBuddiesLayout.OnBuddyItemClickListener
```
....

```java
listBuddies.setOnItemClickListener(this);
```
You will receive the OnItemClick callback in `onBuddyItemClicked` which is similar to `onItemClick` but indicate with the parameter `int buddy` in which of the lists the item clicked is contained.
if the value of `buddy` is 0 the item is on the first list (left) and if it is 1 is on the second list (right).

```java
@Override
    public void onBuddyItemClicked(AdapterView<?> parent, View view, int buddy, int position, long id) {
          //int buddy indicate the list where the item is contain.
          // 0 - left
          // 1 - right
    }
````

Developed By
--------------------

Javier Pardo de Santayana Gómez - <jpardogo@gmail.com>

<a href="https://twitter.com/jpardogo">
  <img alt="Follow me on Twitter"
       src="https://raw.github.com/jpardogo/ListBuddies/master/art/ic_twitter.png" />
</a>
<a href="https://plus.google.com/u/0/+JavierPardo/posts">
  <img alt="Follow me on Google+"
       src="https://raw.github.com/jpardogo/ListBuddies/master/art/ic_google+.png" />
</a>
<a href="http://www.linkedin.com/profile/view?id=155395637">
  <img alt="Follow me on LinkedIn"
       src="https://raw.github.com/jpardogo/ListBuddies/master/art/ic_linkedin.png" />

License
-----------

    Copyright 2013 Javier Pardo de Santayana Gómez

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
[1]: http://www.youtube.com/watch?v=-AWwboC-lis
