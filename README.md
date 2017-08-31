# Navigators

[![](https://jitpack.io/v/everalbum/navigators.svg)](https://jitpack.io/#everalbum/navigators)

Tree-like Navigator framework for Android, inspired by Square's [Coordinators](https://github.com/square/coordinators) library.

## Installing 

Add this to your root build.gradle file:

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
and this to your dependencies:

`compile 'com.github.everalbum:navigators:1.0.1'`

## Introduction

Navigators is a navigation framework that brings Views onto the screen without needing to rely on complicated fragment lifecycles. Navigators
is built in a way to be used in MVP, MVVM, etc...

The framework uses a tree to facilitate navigation. In this tree, a [Coordinator](https://github.com/everalbum/navigators/blob/master/lib/src/main/java/com/everalbum/navigators/Coordinator.java)
is considered a leaf node.  A [Navigator](https://github.com/everalbum/navigators/blob/master/lib/src/main/java/com/everalbum/navigators/Navigator.java) 
is just a coordinator that can have multiple children, including nested navigators. A Navigator can then be considered a regular node in this tree.

To get started, create an instance of the abstract `Navigator` class:

```java
public class ExampleNavigator extends Navigator {

  public ExampleNavigator(PageManager pageManager) {
      super(pageManager);
  }

  @Override
  public int getLayoutRes() {
      return R.layout.layout_example;
  }
}
```

and then pass it the base view group:

```java
FrameLayout container = (FrameLayout) findViewById(R.id.container);
ExampleNavigator navigator = new ExampleNavigator(new ExamplePageManager); // See below for explanation of PageManager
navigator.initialize(container);
```

## Navigation

Each navigator requires a [PageManager](https://github.com/everalbum/navigators/blob/master/lib/src/main/java/com/everalbum/navigators/PageManager.java)
to manage coordinators and the order of pages. A call to `nextPage()` or `previousPage()` will cause the next/previous coordinator to be
brought on to the screen.

```java
int pageIndex = 0;
@Nullable
@Override
public Coordinator nextPage() {
    pageIndex++;
    switch(pageIndex) {
      case 1:
        return new FirstPageCoordinator();
      case 2:
        return new SecondPageCoordinator();
      case 3:
        return new NestedNavigator(new NestedPageManager());
    }
    return null;
}

@Nullable
@Override
public Coordinator previousPage() {
    pageIndex--;
    switch(pageIndex) {
      case 1:
        return new FirstPageCoordinator();
      case 2:
        return new SecondPageCoordinator();
      case 3:
        return new NestedNavigator(new NestedPageManager());
    }
    return null;
}
```

To avoid creating new coordinators and losing state, the use of [CachingPageManager](https://github.com/everalbum/navigators/blob/master/lib/src/main/java/com/everalbum/navigators/CachingPageManager.java)
is recommended.

Note that `nextPage()` and `previousPage()` can both return null. As soon as the page manager returns null for one of these methods, the framework
considers that the page manager has reached the end (or start, if going backward) of its pages.

## Navigation Callbacks

After the page manager reaches the end (or the start, if moving backwards) of its pages, it would be nice to inform the base activity/fragment
to open up the next activity or do something. Registering a [NavigationCallback](https://github.com/everalbum/navigators/blob/master/lib/src/main/java/com/everalbum/navigators/NavigationCallback.java)
with the top-most navigator solves this issue. The registered callback will get called whenever the next or previous pages are loaded.

## Lifecycle

There are two primary lifecycle methods: `attach(View v)` and `detach(View v)`. It's common practice to bind views using ButterKnife and setup your
presenter/whatever in the attach method, and unbind and clean up in the detach method:

```java 
@Override
public void attach(View view) {
    super.attach(view);
    unbinder = ButterKnife.bind(this, view);
    presenter.onStart();
}

@Override
public void detach(View view) {
    super.detach(view);
    presenter.onStop();
    unbinder.unbind();
}
```

There are 4 more optional lifecycle methods that can be used to determine the direction of navigation. These methods are commonly used
to setup any animations before the view comes on screen or leaves.

```java
public void onEnter(View view) {
  // The coordinator is entering in the forward direction (e.g. a previous coordinator requested the next page)
}

public void onReenter(View view) {
  // The coordinator is entering in the backward direction (e.g. the next coordinator requested the previous page)
}

public void onExitForwards(View view) {
  // The coordinator is exiting in the forward direction (e.g. the current coordinator requested the next page)
}

public void onExitBackwards(View view) {
  // The coordinator is exiting in the backward direction (e.g. the current coordinator requested the previous page)
}
```

## Communication with other Coordinators in the tree

Often times, it is important to pass data between coordinators in the tree. To facilate this, Navigators uses a simple immutable map
called a [State](https://github.com/everalbum/navigators/blob/master/lib/src/main/java/com/everalbum/navigators/State.java). Coordinators
are allowed to modify the state and return a new state using the `getEndingState(State state)` method:

```java
@NonNull
@Override
protected State getEndingState(@NonNull State state) {
    return state.putString(EMAIL_KEY, getEmailText());
}
```

All write methods on the state object will create a new State instance (with the old state's data) along with whatever information is written.

Coordinators can peek at the state by calling `getState()` at any time.

## Pros and cons

### Pros

This navigation framework truly shines when you have a known and ordered set of pages to display. For example, an onboarding flow, or a slide show of sorts.

Because the navigation depends strictly on views and does not require any Context/Activity/Fragment, this framework makes it very easy
to separate the view from the presenter in MVP. For example, the coordinator can handle all view related things, including animation, updating
text views, etc.., while the presenter handles all logic. This makes it very easy to unit test.

### Cons

Since a page manager requires the pages to be known ahead of time, the framework only really works for linear flows of UI. Something
like a view pager or tab layout can't be replaced with Navigators. 

Since the library depends solely on views and requires no Context or Activity dependencies, communication with the base activity (or fragment) is 
limited to [NavigationCallback](https://github.com/everalbum/navigators/blob/master/lib/src/main/java/com/everalbum/navigators/NavigationCallback.java).

# License

Copyright (c) 2017 Everalbum Inc

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
