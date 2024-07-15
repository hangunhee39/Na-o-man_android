package com.hgh.na_o_man.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hgh.na_o_man.presentation.component.type.AppBarMenu

@Composable
fun CreateAppBar(
    modifier: Modifier = Modifier,
    onCreateClick: () -> Unit,
) {
    MainAppBar(
        modifier = modifier,
        back = null,
        menu = AppBarMenu.MYPAGE,
        onMenuClick = onCreateClick,
        onBackClick = { },
    )
}
@Composable
fun BackAppBar(
    modifier: Modifier = Modifier,
    onExitClick: () -> Unit,
    onCreateClick: () -> Unit,
) {
    MainAppBar(
        modifier = modifier,
        back = AppBarMenu.BACK,
        menu = AppBarMenu.BACK,
        onMenuClick = onExitClick,
        onBackClick = onCreateClick,
    )
}

@Composable
private fun MainAppBar(
    modifier: Modifier = Modifier,
    back: AppBarMenu? = null,
    menu: AppBarMenu? = null,
    onMenuClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {


        if (back != null) {
            Icon(
                imageVector = ImageVector.vectorResource(id = back.icon),
                tint = Color.Unspecified,
                contentDescription = stringResource(id = back.contentDescription),
                modifier = Modifier
                    .padding(start = back.horizontalPadding)
                    .clip(RoundedCornerShape(30.dp))
                    .clickable { onBackClick() }
                    .align(Alignment.CenterStart),
            )
        }
        if (menu != null) {
            Icon(
                imageVector = ImageVector.vectorResource(id = menu.icon),
                tint = Color.Unspecified,
                contentDescription = stringResource(id = menu.contentDescription),
                modifier = Modifier
                    .padding(end = menu.horizontalPadding)
                    .clip(RoundedCornerShape(30.dp))
                    .clickable { onMenuClick() }
                    .align(Alignment.CenterEnd),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppBarPreview() {
    BackAppBar(
        onCreateClick = {},
        onExitClick = {}
    )
}
