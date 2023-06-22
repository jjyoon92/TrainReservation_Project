package com.sdt.trproject

import android.content.res.TypedArray
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineStart
import java.security.AccessController.getContext
import kotlin.math.roundToInt


open class BaseActivity : AppCompatActivity() {

    // Navigation //////////////////////////////////////////////////////////////////////////////////////
    // toolbarContentView.findViewById 로 뷰 찾기 사용
    private lateinit var container: View;
    private lateinit var sidebar: DrawerLayout;
    private lateinit var navigationView: NavigationView

    private var header: View? = null
    private var footer: View? = null
    private var isEmptyTrailing: Boolean = false
    private var trailing: ViewGroup? = null

    protected val navigationHeader: View
        get() {
            header = header ?: navigationView.getHeaderView(0)
            return header!!
        }
    protected val navigationFooter: View
        get() {
            footer = footer ?: findViewById(R.id.footer,true)
            return footer!!
        }

////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

// Navigation //////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.appbar)

        // toolbarContainer 변수에 toolbar_container 뷰를 할당
        val toolbarContainer = findViewById<FrameLayout>(R.id.toolbar_container, true)
        // container 변수에 activity_main 레이아웃을 inflate하여 생성한 뷰를 할당
        container = layoutInflater.inflate(R.layout.activity_main, null, false)
        // toolbarContainer에 container 뷰를 추가
        toolbarContainer.addView(container)

        // sidebar 변수에 drawer_layout 뷰를 할당
        sidebar = findViewById<DrawerLayout>(R.id.drawer_layout, true)
        // navigationView 변수에 nav_view 뷰를 할당
        navigationView = findViewById(R.id.nav_view, true)

        // toolbar 변수에 toolbar 뷰를 할당
        val toolbar = findViewById<Toolbar>(R.id.toolbar, true)

        // trailing 변수에 toolbar에서 trailing_ll 뷰를 찾아 할당
        trailing = toolbar.findViewById<ViewGroup>(R.id.trailing_ll)
        useEmptyTrailing()


        // toolbar에서 nav_btn 뷰를 찾아 클릭 리스너를 설정.
        // 클릭하면 sidebar의 상태를 변경(열기/닫기)
        toolbar.findViewById<View>(R.id.nav_btn).apply {
            setOnClickListener {
                if (!sidebar.isDrawerOpen(GravityCompat.START)) {
                    sidebar.openDrawer(GravityCompat.START)
                } else {
                    sidebar.closeDrawer(GravityCompat.START)
                }
            }
        }

        setSupportActionBar(toolbar)
////////////////////////////////////////////////////////////////////////////////////////////////////
    }


    // navigationView의 메뉴에서 idx 인덱스에 해당하는 아이템을 반환하는 메소드
    protected fun getNavigationHeaderMenuItem(idx: Int): MenuItem {
        // 메뉴 아이템 클릭 리스너 : .setOnMenuItemClickListener {}
        return navigationView.menu[idx]
    }

    // trailing에 새로운 뷰를 추가하는 메소드. layout을 인플레이트하여 새로운 뷰를 생성하고,
    // 이 뷰의 크기를 설정한 후 trailing에 추가하고, 추가된 뷰를 반환.
    protected fun addTrailing(
        @LayoutRes layout: Int
    ): View? {
        val trailing = this.trailing ?: return null
        if(isEmptyTrailing) {
            trailing.removeAllViews()
            isEmptyTrailing = false
        }

        val v: View = layoutInflater.inflate(layout, null, false)
        setDefaultMenuItemSize(v)
        trailing.addView(v)

        return v
    }
    // isEmptyTrailing 변수를 true로 설정하고 trailing에 새로운 뷰를 추가하는 메소드
    private fun useEmptyTrailing() {
        isEmptyTrailing = true
        val trailing = this.trailing ?: return
        val v: View = View(this)
        setDefaultMenuItemSize(v)
        trailing.addView(v)
    }
        // 주어진 뷰의 크기를 설정하는 메소드
    private fun setDefaultMenuItemSize(v: View?) {
        val displayMetrics = DisplayMetrics()

        // 안드로이드 버전에 따라 디스플레이의 메트릭스를 얻는 방법이 다릅니다.
        // 버전 R 이상이라면 display를 이용하여 메트릭스를 얻고,
        // 그렇지 않다면 windowManager를 이용하여 메트릭스를 얻습니다.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            //defaultDisplay = getSystemService<DisplayManager>()?.getDisplay(Display.DEFAULT_DISPLAY)
            val display = this.display
            display?.getRealMetrics(displayMetrics)
        } else {
            @Suppress("DEPRECATION")
            val display = this.windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(displayMetrics)
        }
        //val dpi = displayMetrics.densityDpi // px = dp * dpi / 160
        val density = displayMetrics.density // px = dp * density

        // v의 크기를 설정. width는 밀도에 따라 계산하고, height는 trailing이 비어있다면 1,
        // 그렇지 않다면 액션바의 크기로 설정.
        v?.layoutParams = ViewGroup.LayoutParams(
            (24 * density).roundToInt(),
            if(isEmptyTrailing) {
                1
            } else {
                val styledAttributes: TypedArray = theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
                val actionBarSize = styledAttributes.getDimension(0, 0f).toInt()
                styledAttributes.recycle()

                actionBarSize
            },
        )
    }
    // findViewById 메소드를 오버라이드. container에서 주어진 id를 가진 뷰를 찾아 반환.
    override fun <T : View?> findViewById(id: Int): T {
        return container.findViewById(id)
    }
    // fromRoot 매개변수에 따라 뷰를 찾는 범위가 다르게 동작하도록 하는 메소드.
    // fromRoot가 true라면 최상위 뷰에서 찾고, false라면 container에서 찾아 반환.
    fun <T : View?> findViewById(id: Int, fromRoot: Boolean = true): T {
        return if(fromRoot) { super.findViewById(id) } else { container.findViewById(id) }
    }
}