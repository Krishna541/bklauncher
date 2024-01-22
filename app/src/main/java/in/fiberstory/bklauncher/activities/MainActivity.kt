package `in`.fiberstory.bklauncher.activities

import `in`.fiberstory.bklauncher.R
import `in`.fiberstory.bklauncher.interfaces.NavigationMenuCallback
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.androijo.tvnavigation.NavigationMenu
import com.androijo.tvnavigation.interfaces.BackPressedStateChange
import com.androijo.tvnavigation.interfaces.FragmentChangeListener
import com.androijo.tvnavigation.interfaces.NavigationStateListener
import com.androijo.tvnavigation.utils.Constants
import com.androijo.tvnavigation.utils.Constants.backCount

class MainActivity : FragmentActivity(), NavigationStateListener, FragmentChangeListener,
    NavigationMenuCallback,BackPressedStateChange {

    companion object {
        lateinit var context: Context
    }

    private lateinit var searchFragment: SearchBarFragment
    private lateinit var liveChannelFragment: LiveChannelFragment
    private lateinit var songsFragment: SongsFragment
    private lateinit var artistsFragment: ArtistsFragment
    private lateinit var podcastFragment: PodcastFragment
    private lateinit var homeFragment: HomeFragment
    private lateinit var settingsFragment: SettingsFragment
    private lateinit var navMenuFragment: NavigationMenu
    private var currentSelectedFragment = Constants.nav_menu_songs
    private var isStackEmpty = false
    private var isNavExpand = false
    var settingFirstFocus =  1
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        context = this@MainActivity

        navMenuFragment = NavigationMenu()
        fragmentReplacer(nav_fr,navMenuFragment)
        homeFragment = SongsFragment()
        fragmentReplacer(R.id.main_FL, homeFragment)
    }

    private fun fragmentReplacer(containerId: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(containerId, fragment).commit()
    }

    /**
     * communication from left-side navigation to right-side content
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStateChanged(expanded: Boolean, lastSelected: String?) {
        isNavExpand = expanded
        if (!expanded) {
            nav_fragment.setBackgroundResource(R.drawable.ic_nav_bg_closed)
            nav_fragment.clearFocus()

            if(lastSelected == "Settings") {
                if (settingsFragment.getFlag()){

                    settingsFragment.restoreFocusOnHelpOkBtn()

                }
                else {
                    settingsFragment.restoreFocusOnPrivacy()
                }
            }

            if(lastSelected == "Profile") {
                profileFragment.restoreDrawableFocusOnLogout()
            }

            when (currentSelectedFragment) {
                Constants.nav_menu_search -> {
                    currentSelectedFragment = Constants.nav_menu_search
                }
                Constants.nav_menu_live -> {
                    currentSelectedFragment = Constants.nav_menu_live
                }
                Constants.nav_menu_songs -> {
                    currentSelectedFragment = Constants.nav_menu_songs
                    songsFragment.restoreSelection()
                }
                Constants.nav_menu_artists -> {
                    currentSelectedFragment = Constants.nav_menu_artists
                }
                Constants.nav_menu_podcasts -> {
                    currentSelectedFragment = Constants.nav_menu_podcasts
                }
                Constants.nav_menu_settings -> {
                    currentSelectedFragment = Constants.nav_menu_settings
                }

            }
        } else {
            //do
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun switchFragment(fragmentName: String?) {
        findViewById<FrameLayout>(R.id.nav_fragment).setBackgroundResource(R.drawable.ic_nav_bg_closed)
        isStackEmpty = true
        when (fragmentName) {
            Constants.nav_menu_search -> {
                searchFragment = SearchBarFragment()
                fragmentReplacer(main_FL.id, searchFragment)
            }
            Constants.nav_menu_live -> {
                liveChannelFragment = LiveChannelFragment()
                fragmentReplacer(main_FL.id, liveChannelFragment)
            }
            Constants.nav_menu_songs -> {
                songsFragment = SongsFragment()
                fragmentReplacer(main_FL.id, SongsFragment())
                songsFragment.restoreSelection()
            }
            Constants.nav_menu_artists -> {
                artistsFragment = ArtistsFragment()
                fragmentReplacer(main_FL.id, ArtistsFragment())
                songsFragment.restoreSelection()
            }
            Constants.nav_menu_podcasts -> {
                podcastFragment = PodcastFragment()
                fragmentReplacer(main_FL.id, PodcastFragment())
                songsFragment.restoreSelection()
            }
            Constants.nav_menu_settings -> {
                settingsFragment = SettingsFragment()
                fragmentReplacer(main_FL.id, settingsFragment)
            }

        }
    }

    override fun navMenuToggle(toShow: Boolean) {

        try {
            if (toShow) {
                nav_fragment.setBackgroundResource(R.drawable.ic_nav_bg_open)
                main_FL.clearFocus()
                nav_fragment.requestFocus()
                navEnterAnimation()
                navMenuFragment.openNav()
            } else {
                nav_fragment.setBackgroundResource(R.drawable.ic_nav_bg_closed)
                nav_fragment.clearFocus()
                main_FL.requestFocus()
                navMenuFragment.closeNav()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                val fragment_left = supportFragmentManager.findFragmentById(R.id.main_FL)
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                val fragment_down = supportFragmentManager.findFragmentById(R.id.main_FL)
            }
        }
        return false

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onAttachFragment(fragment: Fragment) {

        when (fragment) {
            is SearchBarFragment -> {
                fragment.setNavigationMenuCallback(this)
            }
            is LiveChannelFragment -> {
                fragment.setNavigationMenuCallback(this)
            }
            is SongsFragment -> {
                fragment.setNavigationMenuCallback(this)
            }
            is ArtistsFragment -> {
                fragment.setNavigationMenuCallback(this)
            }
            is PodcastFragment -> {
                fragment.setNavigationMenuCallback(this)
            }
            is SettingsFragment -> {
                fragment.setNavigationMenuCallback(this)
            }
            is NavigationMenu -> {
                fragment.setFragmentChangeListener(this)
                fragment.setNavigationStateListener(this)
                fragment.setBackPressedListener(this)
            }
        }
    }


    private fun navEnterAnimation() {
        val animate = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
        nav_fragment.startAnimation(animate)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        super.finish()
    }

    //2
    override fun backPressedStateChange() {
        if (isStackEmpty) {
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
            backCount = 0
            isStackEmpty = false
        }else{
            if(isNavExpand){
                    finish()

            }
        }
    }

}
