# Sapivents - Sapientia events
# Android application
## You can find the project specification [here](https://github.com/andraspatka/Sapivents/blob/development/Documentation/Specification.pdf)
## [Project styleguide](https://github.com/andraspatka/Sapivents/blob/development/Documentation/Styleguide.pdf)

# Some explanation to the classes in the util package:
## [EventPrefUtil.java](https://github.com/andraspatka/Sapivents/blob/development/app/src/main/java/szamtech/fejer_patka/ms/sapientia/ro/sapivents/utils/EventPrefUtil.java)
This class is used for storing Event objects in SharedPreferences.<br/>  Will be removed once Firebase is fully integrated into the project
## [FragmentNavigationUtil.java](https://github.com/andraspatka/Sapivents/blob/development/app/src/main/java/szamtech/fejer_patka/ms/sapientia/ro/sapivents/utils/FragmentNavigationUtil.java)
This class is used for fragment management with bottom navigation. <br/>
Has four constant fields: 
```java
    //Number of bottom navigation elements
    public static final int NUMBER_OF_SCREENS = 3;
    //Bottom navigation element indexes
    public static final int ACCOUNT_SCREEN = 0;
    public static final int HOME_SCREEN = 1;
    public static final int ADD_SCREEN = 2;
```
Each of these constant values are an index in this data structure:
```java
    //Holds the fragment stacks for each screen
    private static ArrayList<Stack<Fragment>> sFragmentStacks = new ArrayList<>();
```
![Bottom navigation](\Documentation\md-res\bottomNav.PNG)<br/>
Each stack corresponds to a bottom navigation elemenet (tab)<br/>
When a tab is selected this method is invoked:<br/>
Basically if a tab was selected and there are no Fragment entries for that tab in sFragmentStacks then the fragment parameter is added to the required stack.<br/>
If there are entries for that Tab then they are loaded to the backstack.
```java
    public static void screenSelected(Context context, Fragment fragment, int viewId, int screen)throws NoSuchScreenException{
            if(screen > NUMBER_OF_SCREENS || screen < 0){
                throw new NoSuchScreenException(screen);
            }
            //Get the fragment manager
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            //Clear the backstack
            fragmentManager.popBackStack();

            //If the fragmentStack at the given screen is empty, add the fragment
            if(sFragmentStacks.get(screen).isEmpty()){
                fragmentManager
                        .beginTransaction()
                        .replace(viewId,fragment)
                        .addToBackStack(screen+"")
                        .commit();
                sFragmentStacks.get(screen).add(fragment);
            }else{//Load the fragments from the stack
                for(int i=0; i<sFragmentStacks.get(screen).size(); ++i){
                    fragmentManager
                            .beginTransaction()
                            .replace(viewId,sFragmentStacks.get(screen).get(i))
                            .addToBackStack(screen+"")
                            .commit();
                }
            }
        }
```
NoSuchScreenException is a custom RuntimeException:
```java
    private static class NoSuchScreenException extends RuntimeException{
            public NoSuchScreenException(int screen){
                super("Invalid Screen: " + screen);
            }
        }
```
Add fragment to a given screen (tab)
```java
    public static void addFragmentToScreen(Context context, Fragment fragment, int viewId, int screen) throws NoSuchScreenException{
            if(screen > NUMBER_OF_SCREENS || screen < 0){
                throw new NoSuchScreenException(screen);
            }
            //Get the bottom navigation element
            BottomNavigationView bottomNavigationView = (BottomNavigationView) ((Activity) context).findViewById(R.id.bottom_nav);
            //Select the appropriate bottom navigation element
            switch(screen){
                case HOME_SCREEN:
                    bottomNavigationView.setSelectedItemId(R.id.menu_home);
                    break;
                case ACCOUNT_SCREEN:
                    bottomNavigationView.setSelectedItemId(R.id.menu_account);
                    break;
                case ADD_SCREEN:
                    bottomNavigationView.setSelectedItemId(R.id.menu_add);
                    break;
            }
            //Add the screen to the stack
            sFragmentStacks.get(screen).add(fragment);
            //Get the FragmentManager
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            //Add the fragment to the backstack, using a tag (tag = screen + "")
            fragmentManager
                    .beginTransaction()
                    .replace(viewId, fragment)
                    .addToBackStack(screen + "")
                    .commit();
    }
```
Pop off a fragment<br/>
Returns false if the fragment is the last one in the given screen. True otherwise
```java
    public static boolean popFragment(Context context, int viewId){
            //Get the FragmentManager
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            //Get the last backstack entry
            FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1);
            //Check to see in which screen the last backstack entry was
            String screenStr = backStackEntry.getName();
            int screen = Integer.parseInt(screenStr);
            if(sFragmentStacks.get(screen).size() >= 2){
                sFragmentStacks.get(screen).pop();

                fragmentManager
                        .beginTransaction()
                        .replace(viewId, sFragmentStacks.get(screen).peek())
                        .addToBackStack(screen + "")
                        .commit();
                return true;
            }else return false;
        }
```
The return value from popFragment() is used here
```java
    @Override
    public void onBackPressed() {
        //FragmentNavigationUtil.popFragment returns false if there is only one fragment in the stack
        //In this case, a back press exits the application
        if(!FragmentNavigationUtil.popFragment(this,R.id.fragment_place)){
            this.finishAffinity();
            Log.v(TAG,"Only fragment");
        }
    }
```
## EventsAdapter.java
RecyclerView.Adapter used for listing Events.

## [DateTime.java](https://github.com/andraspatka/Sapivents/blob/development/app/src/main/java/szamtech/fejer_patka/ms/sapientia/ro/sapivents/beans/DateTime.java)
I had a lot of trouble finding built-in Date class which isn't deprecated and is already supported in Android 6.0 (sdk level 23) <br/>
<b>So I decided to write my own</b><br/>
I store the date in the following format: <b>YYYY-MM-DDThh:mm</b><br/>
The goal for this class is to have the DateTime be <b>ONLY</b> a string when Serialized <br/>
I do have year, month, day, hour, minutes attributes as well, but these are transient, which means that they will be ignored during serialization



