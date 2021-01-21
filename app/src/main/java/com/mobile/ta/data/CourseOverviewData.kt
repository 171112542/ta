package com.mobile.ta.data

import com.mobile.ta.R
import com.mobile.ta.model.CourseOverview
import com.mobile.ta.model.LevelTag
import com.mobile.ta.model.TypeTag

object CourseOverviewData {
    val data = arrayOf(
        CourseOverview(
            id = 1,
            title = "Blackhole",
            description = "A black hole is a place in space where gravity pulls so much that even light can not get out. The gravity is so strong because matter has been squeezed into a tiny space. This can happen when a star is dying. Space telescopes with special tools can help find black holes.",
            level = LevelTag.JUNIOR_ONE,
            type = TypeTag.PHYSICS,
            imageUrl = R.drawable.sample_cover_physics
        ),
        CourseOverview(
            id = 2,
            title = "Math",
            description = "Mathematics is the science that deals with the logic of shape, quantity and arrangement. Math is all around us, in everything we do. It is the building block for everything in our daily lives, including mobile devices, architecture (ancient and modern), art, money, engineering, and even sports.",
            level = LevelTag.JUNIOR_THREE,
            type = TypeTag.MATH,
            imageUrl = R.drawable.sample_cover_math
        ),
        CourseOverview(
            id = 3,
            title = "Atoms",
            description = "An atom is a particle of matter that uniquely defines achemical element. An atom consists of a central nucleus that is usually surrounded by one or more electrons. Each electron is negatively charged. The nucleus is positively charged, and contains one or more relatively heavy particles known as protons and neutrons.",
            level = LevelTag.SENIOR_TWO,
            type = TypeTag.CHEMISTRY,
            imageUrl = R.drawable.sample_cover_chemistry
        ),
        CourseOverview(
            id = 4,
            title = "Plants",
            description = "Plants are multicellular organisms in the kingdom Plantae that use photosynthesis to make their own food. They produce most of the world's oxygen, and are important in the food chain, as many organisms eat plants or eat organisms which eat plants.",
            level = LevelTag.SENIOR_THREE,
            type = TypeTag.BIOLOGY,
            imageUrl = R.drawable.sample_cover_biology
        )
    )
}