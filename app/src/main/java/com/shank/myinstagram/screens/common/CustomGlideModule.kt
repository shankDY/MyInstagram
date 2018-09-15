package com.shank.myinstagram.screens.common

import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

//Приложения могут добавить соответствующую аннотированную AppGlideModule реализацию для создания
// свободного API, который включает большинство опций, в том числе те, которые определены
// в библиотеке:
//для подключения api glide
@GlideModule
class CustomGlideModule: AppGlideModule()