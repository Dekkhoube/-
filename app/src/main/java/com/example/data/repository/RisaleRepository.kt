package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository providing authorized digital non-video texts, treatises,
 * contextual Lügat dictionary, thematic Q&A, and study guides from the
 * Risale-i Nur corpus and Islamic classical scholarship.
 */
object RisaleRepository {

    val books: List<RisaleBook> = listOf(
        RisaleBook(
            id = "sozler",
            titleOriginal = "Sözler",
            titleBengali = "বাণীসমূহ (সোজলের)",
            titleEnglish = "The Words",
            category = "প্রধান রচনাবলী",
            descriptionBengali = "ঈমানের মৌলিক ভিত্তি, তাওহিদ, নামাজ, পরকাল এবং বিশ্বজগতের সৃষ্টিরহস্যের গভীর তাৎপর্য বিশ্লেষণ।",
            descriptionEnglish = "Foundational treatises on belief, Divine unity, prayer, resurrection, and the secrets of the cosmos.",
            totalChapters = 33,
            pagesCount = 750,
            coverAccentColorHex = 0xFF1B5E20
        ),
        RisaleBook(
            id = "hastalar",
            titleOriginal = "Hastalar Risalesi",
            titleBengali = "অসুস্থদের প্রতি সান্ত্বনা (হাসতালার)",
            titleEnglish = "Treatise for the Sick",
            category = "চিকিৎসা ও আধ্যাত্মিকতা",
            descriptionBengali = "অসুস্থতা ও কষ্টের আধ্যাত্মিক মর্যাদা, ধৈর্য এবং ২৫টি অমূল্য আত্মিক প্রতিষেধক ও শান্তিদায়ক সমাধান।",
            descriptionEnglish = "25 spiritual remedies providing peace, patience, and divine wisdom for those facing illness and hardship.",
            totalChapters = 25,
            pagesCount = 85,
            coverAccentColorHex = 0xFF00695C
        ),
        RisaleBook(
            id = "ihlas",
            titleOriginal = "İhlas Risalesi",
            titleBengali = "ইখলাস ও নিষ্ঠা (ইহলাস)",
            titleEnglish = "Treatise on Sincerity",
            category = "আত্মশুদ্ধি",
            descriptionBengali = "দ্বীনি কাজে একনিষ্ঠতা অর্জন, আত্মপ্রবঞ্চনা ও পারস্পরিক হিংসা-বিদ্বেষ থেকে মুক্ত থাকার অমোঘ নিয়মাবলী।",
            descriptionEnglish = "Essential principles of pure intention, selflessness, and spiritual harmony in religious service.",
            totalChapters = 4,
            pagesCount = 45,
            coverAccentColorHex = 0xFF0D47A1
        ),
        RisaleBook(
            id = "genclik",
            titleOriginal = "Gençlik Rehberi",
            titleBengali = "যুবকদের পথনির্দেশিকা (গেঞ্চলিক রেহবেরি)",
            titleEnglish = "Guide for Youth",
            category = "যুব ও সমাজ",
            descriptionBengali = "বর্তমান আধুনিক যুগে তারুণ্যের উচ্ছ্বাস ও আত্মিক চরিত্র রক্ষা এবং চিরস্থায়ী সফলতার পথনির্দেশ।",
            descriptionEnglish = "Guidance for young Muslims on chastity, moral integrity, and eternal happiness amid worldly distractions.",
            totalChapters = 8,
            pagesCount = 60,
            coverAccentColorHex = 0xFFE65100
        ),
        RisaleBook(
            id = "asa_yi_musa",
            titleOriginal = "Asâ-yı Mûsâ",
            titleBengali = "মুসার লাঠি (আসা-য়ী মূসা)",
            titleEnglish = "The Staff of Moses",
            category = "ধর্মতত্ত্ব ও দর্শন",
            descriptionBengali = "ঈমানের ফলশ্রুতি, সৃষ্টিজগতের বিজ্ঞানসম্মত প্রমাণ এবং নাস্তিকতার বিরুদ্ধে অকাট্য যৌক্তিক দলিল।",
            descriptionEnglish = "Evidences of belief and the fruits of faith destroying materialistic doubt through scientific observation.",
            totalChapters = 11,
            pagesCount = 220,
            coverAccentColorHex = 0xFF4A148C
        ),
        RisaleBook(
            id = "lemalar_munacat",
            titleOriginal = "Lem'alar - Münâcât",
            titleBengali = "মোনাজাত ও ঐশ্বরিক আলোকমালা",
            titleEnglish = "The Flashes & Supplications",
            category = "দোয়া ও তাসাউফ",
            descriptionBengali = "ইউনুস (আ.) ও আইয়ুব (আ.)-এর দোয়ার বাস্তবতা এবং চরম বিপদে আল্লাহর নৈকট্য লাভের আত্মিক আলো।",
            descriptionEnglish = "Spiritual contemplations on the supplications of Prophets Yunus and Ayyub (peace be upon them).",
            totalChapters = 15,
            pagesCount = 310,
            coverAccentColorHex = 0xFF3E2723
        )
    )

    // Detailed chapter data with multi-language sections
    private val chaptersMap: Map<String, List<RisaleChapter>> = mapOf(
        "sozler" to listOf(
            RisaleChapter(
                id = "sozler_ch_1",
                bookId = "sozler",
                chapterNumber = 1,
                titleOriginal = "Birinci Söz: Bismillah Her Hayrın Başıdır",
                titleBengali = "প্রথম বাণী: 'বিসমিল্লাহ' প্রতিটি কল্যাণের সূচনা",
                titleEnglish = "The First Word: 'Bismillah' is the Beginning of All Good",
                introductionBengali = "বিসমিল্লাহির রাহমানির রাহিম — এটি এমন এক বরকতময় চাবি যা মহাবিশ্বের সব বন্ধ দুয়ার খুলে দেয় এবং অফুরন্ত শক্তির সংযোগ ঘটায়।",
                sections = listOf(
                    RisaleSection(
                        id = "sozler_1_1",
                        sectionNumber = 1,
                        subHeadingBengali = "বিশ্বজগতের অফুরন্ত শক্তির চাবিকাঠি",
                        subHeadingEnglish = "The Key to Universal Treasury",
                        textOriginal = "Bismillah her hayrın başıdır. Biz dahi başta ona başlarız. Bil ey nefsim, şu mübarek kelime İslâm nişanı olduğu gibi, bütün mevcudatın lisan-ı haliyle vird-i zebanıdır.",
                        textBengali = "'বিসমিল্লাহ' প্রতিটি কল্যাণকর কাজের সূচনা। আমরাও এই বরকতময় শব্দ দিয়েই শুরু করি। হে আমার নফস! জেনে রাখো, এই পবিত্র বাক্যটি কেবল ইসলামের নিশানাই নয়, বরং গোটা সৃষ্টিজগতের মৌন ভাষার অবিরাম জিকির।\n\n'বিসমিল্লাহ' কত অসীম এক শক্তি এবং অফুরন্ত বরকত ধারণ করে তা বুঝতে হলে একটি প্রতীকী কাহিনীর প্রতি লক্ষ্য করো: মরুভূমিতে পরিভ্রমণকারী দুজন মানুষের মতো একজন অহংকারী ও উদ্ধত, আরেকজন নম্র ও বিনয়ী। উদ্ধত ব্যক্তি কোনো গোত্রপ্রধান বা রাজার আশ্রয় না নিয়ে একাকী যাত্রা করে ভীতি ও বিপদের সম্মুখীন হয়। কিন্তু বুদ্ধিমান ব্যক্তি একজন শক্তিশালী নেতার নামে সফর করে নিরাপদে সকল বাধা অতিক্রম করে।",
                        textEnglish = "Bismillah is the beginning of all good. We too commence with it. Know, O my soul, that just as this blessed phrase is a hallmark of Islam, so too is it the constantly recited prayer of all beings in the language of their disposition.\n\nTo comprehend what an inexhaustible strength and infinite blessing Bismillah is, consider this traveler analogy: One traveler travels through the desert in reliance upon a powerful governor's name, while the other journeys independently in pride. The former encounters no peril, whereas the latter is continually terrorized.",
                        quranicReference = "সূরা ফাতিহা ১:১ - بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        hadithReference = "যে কাজ বিসমিল্লাহ ব্যতীত শুরু হয়, তা বরকতহীন ও অপূর্ণাঙ্গ। (মুসনাদে আহমাদ)"
                    ),
                    RisaleSection(
                        id = "sozler_1_2",
                        sectionNumber = 2,
                        subHeadingBengali = "গাছপালা ও প্রাণীদের বিসমিল্লাহ",
                        subHeadingEnglish = "The Bismillah of Nature",
                        textOriginal = "Bütün mevcudat lisan-ı hal ile 'Bismillah' der. Meyvedar ağaçlar ve nebatat, rahmetin tecellisiyle bize rızık taşırlar.",
                        textBengali = "সমস্ত সৃষ্টিজগৎ তাদের স্বভাবজাত অবস্থায় 'বিসমিল্লাহ' বলে। রেশমপোকার মতো একটি দুর্বল ক্ষুদ্র প্রাণী কোমল পাতার আহার করে বিশ্বের সর্বশ্রেষ্ঠ রেশম বস্ত্র উপহার দেয়। বিষাক্ত মৌমাছি অপূর্ব মিষ্টি মধু দান করে। শুকনো মাটির ভেতর থেকে শত শত মিষ্টি সুস্বাদু ফলমূল প্রস্ফুটিত হয়। এরা প্রত্যেকেই বিশ্বপালক মহান আল্লাহর দয়ায় 'বিসমিল্লাহ' বলে মানবজাতির সামনে খাদ্য পরিবেশন করছে। অতএব আমরাও আল্লাহর নামে গ্রহণ করব এবং আল্লাহর নামেই ব্যয় করব।",
                        textEnglish = "All created entities utter 'Bismillah' in the language of their state. The tiny silkworm without hands weaves royal silk from bitter mulberry leaves. The bee produces pure nourishing honey. Barren trees bear luscious fruits. All act as agents in the name of the Divine Sustainer.",
                        quranicReference = "সূরা আন-নাহল ১৬:৬৯"
                    )
                )
            ),
            RisaleChapter(
                id = "sozler_ch_2",
                bookId = "sozler",
                chapterNumber = 2,
                titleOriginal = "İkinci Söz: İmanın İnsana Kazandırdığı Huzur",
                titleBengali = "দ্বিতীয় বাণী: ঈমানের মাধ্যমে মানবাত্মার পরম প্রশান্তি",
                titleEnglish = "The Second Word: Peace Granted Through Faith",
                introductionBengali = "ঈমান মানবজাতিকে সৃষ্টিজগতের রাজা ও মহাবিশ্বের প্রতিনিধি হিসেবে সম্মানিত করে।",
                sections = listOf(
                    RisaleSection(
                        id = "sozler_2_1",
                        sectionNumber = 1,
                        subHeadingBengali = "বিশ্বাস বনাম সংশয়",
                        subHeadingEnglish = "Faith vs. Doubt",
                        textOriginal = "İman hem nurdur, hem kuvvettir. Hakiki imanı elde eden adam, kâinata meydan okuyabilir.",
                        textBengali = "ঈমান কেবল একটি ধারণা নয়, ঈমান হলো আলো এবং ঈমান হলো অপ্রতিরোধ্য শক্তি। যে ব্যক্তি খাঁটি ঈমান অর্জন করতে সক্ষম হয়, সে সমগ্র মহাবিশ্বের প্রতিকূলতাকে জয় করতে পারে এবং জীবনের সকল দুঃসময়ে পরম শান্তিতে অবিচল থাকে। কারণ সে জানে, মহাবিশ্বের অনু পরমাণু থেকে শুরু করে নক্ষত্ররাজি পর্যন্ত সবকিছু এক করুণাময় প্রতিপালকের কর্তৃত্বাধীন।",
                        textEnglish = "Faith is both light and strength. The person who attains true faith can challenge the entire universe and remain unshakable in adversity, knowing that every particle operates under the absolute sovereignty of the Merciful Creator.",
                        quranicReference = "সূরা আল-বাকারা ২:২৫৭ - اللَّهُ وَلِيُّ الَّذِينَ آمَنُوا يُخْرِجُهُم مِّنَ الظُّلُمَاتِ إِلَى النُّورِ"
                    )
                )
            ),
            RisaleChapter(
                id = "sozler_ch_3",
                bookId = "sozler",
                chapterNumber = 3,
                titleOriginal = "Üçüncü Söz: İbadet ve Namazın Hikmeti",
                titleBengali = "তৃতীয় বাণী: ইবাদত ও পাঁচ ওয়াক্ত নামাজের রহস্য",
                titleEnglish = "The Third Word: The Wisdom of Worship and Daily Prayers",
                introductionBengali = "দৈনিক পাঁচ ওয়াক্ত নামাজ মানবাত্মার খোরাক এবং ক্ষণস্থায়ী দুনিয়ার কাজকে চিরস্থায়ী আখিরাতের পুঁজিতে রূপান্তরিত করে।",
                sections = listOf(
                    RisaleSection(
                        id = "sozler_3_1",
                        sectionNumber = 1,
                        subHeadingBengali = "২৪ ঘণ্টার ভেতর মাত্র ১ ঘণ্টা আল্লাহর জন্য",
                        subHeadingEnglish = "Just One Hour Out of Twenty-Four",
                        textOriginal = "Namaz ne kadar kıymettar ve mühim, hem ne kadar ucuz ve az bir masrafla kazanılır.",
                        textBengali = "নামাজ কত মূল্যবান ও সুমহান এক সম্পদ এবং তা অর্জন করতে কত সামান্য সময় ও শক্তি ব্যয় হয়! মহান আল্লাহ মানুষকে প্রতিদিন ২৪টি স্বর্ণমুদ্রা সদৃশ ২৪টি ঘণ্টা দান করেছেন। এই ২৪ ঘণ্টার মধ্যে মাত্র ১ ঘণ্টা যদি অযুসহ পাঁচ ওয়াক্ত নামাজে নিবেদিত করা হয়, তবে বাকি ২৩ ঘণ্টার জাগতিক বৈধ কাজকর্মও পরকালে অনন্ত সওয়াবের ইবাদতে পরিগণিত হয়। যে ব্যক্তি ২৪ ঘণ্টার ১ ঘণ্টাও নিজের আত্মিক জীবনের জন্য ব্যয় করে না, সে চরম আত্মপ্রবঞ্চনায় নিমজ্জিত।",
                        textEnglish = "Prayer is an immense treasure acquired at minimal cost. The Almighty bestows 24 golden hours each day. Dedicating merely a single hour to the five daily prayers sanctifies the remaining 23 hours of lawful daily life into perpetual worship.",
                        quranicReference = "সূরা ত্বা-হা ২০:১৪ - وَأَقِمِ الصَّلَاةَ لِذِكْرِي"
                    )
                )
            )
        ),
        "hastalar" to listOf(
            RisaleChapter(
                id = "hastalar_ch_1",
                bookId = "hastalar",
                chapterNumber = 1,
                titleOriginal = "Birinci Deva: Hastalık Bir İhsan-ı İlâhîdir",
                titleBengali = "প্রথম প্রতিষেধক: রোগব্যাধি আল্লাহর এক বিশেষ উপহার",
                titleEnglish = "First Remedy: Illness is a Divine Gift",
                introductionBengali = "হে ধৈর্যহীন অসুস্থ ভাই ও বোন! নিরাশ হয়ো না; অসুস্থতা কোনো অভিশাপ নয়, বরং আত্মিক শুদ্ধির এক মহৌষধ।",
                sections = listOf(
                    RisaleSection(
                        id = "hastalar_1_1",
                        sectionNumber = 1,
                        subHeadingBengali = "গুনাহ মোচন ও আত্মিক পরিপক্কতা",
                        subHeadingEnglish = "Expiation of Sins",
                        textOriginal = "Ey bîçare hasta! Merak etme, sabret. Senin hastalığın sana dert değil, belki bir nevi dermandır.",
                        textBengali = "হে অসহায় রোগী! উদ্বিগ্ন হয়ো না, ধৈর্য ধারণ করো। তোমার এই রোগটি তোমার জন্য কোনো ক্ষতি নয়, বরং এক প্রকার আত্মিক প্রতিষেধক। কেননা ক্ষণস্থায়ী পার্থিব জীবনে মানুষ যখন সুস্থ ও নিরবচ্ছিন্ন সুখ ভোগ করে, তখন সে প্রায়শই আল্লাহর কথা ও মৃত্যু ভুলে উদাসীন হয়ে পড়ে। রোগব্যাধি সেই অলসতা ভেঙে মানুষকে আল্লাহর দরবারে বিনীত করে এবং অতীতের কৃত গুনাহসমূহ মোচন করে আমলনামা উজ্জ্বল করে।",
                        textEnglish = "O afflicted patient! Do not despair; be patient. Your illness is not a misfortune, but rather a spiritual cure. Unbroken physical health often induces heedlessness, whereas illness awakens the soul and purges accumulated sins.",
                        hadithReference = "কোনো মুসলিমের সামান্য ক্লান্তি, রোগ, শোক বা কাঁটার আঘাত লাগলেও আল্লাহ তার পাপ মার্জনা করেন। (সহিহ বুখারি)"
                    )
                )
            ),
            RisaleChapter(
                id = "hastalar_ch_2",
                bookId = "hastalar",
                chapterNumber = 2,
                titleOriginal = "İkinci Deva: Sabır Kuvvetini Dağıtmamak",
                titleBengali = "দ্বিতীয় প্রতিষেধক: ধৈর্যের শক্তিকে বিভক্ত না করা",
                titleEnglish = "Second Remedy: Conserving the Power of Patience",
                introductionBengali = "অতীতের কষ্ট শেষ হয়ে গেছে এবং ভবিষ্যতের কষ্ট এখনো আসেনি; শুধুমাত্র বর্তমান মুহূর্তের জন্য ধৈর্য যথেষ্ট।",
                sections = listOf(
                    RisaleSection(
                        id = "hastalar_2_1",
                        sectionNumber = 1,
                        subHeadingBengali = "বর্তমান মুহূর্তের জন্য ধৈর্য",
                        subHeadingEnglish = "Patience for the Present Moment",
                        textOriginal = "Sabır kuvvetini sağa sola, yani geçmişe ve geleceğe dağıtma. Yalnız hâl-i hazıra sarf et.",
                        textBengali = "মানুষ যখন অসুস্থ হয়, শয়তান তাকে ভয় দেখায়: 'হায়! আমি এতদিন ধরে কষ্ট পেলাম, সামনে আরও কত দিন না জানি এই কষ্ট চলবে!' এই ভেবে সে মানসিকভাবে ভেঙে পড়ে। অথচ অতীত কষ্ট এখন সুখস্মৃতি কারণ তা পার হয়ে গেছে, আর ভবিষ্যতের দিনগুলোর কষ্ট এখনও বাস্তবে অস্তিত্বহীন। আল্লাহ তোমাকে যে ধৈর্যশক্তি দিয়েছেন তা বর্তমান মুহূর্তের জন্য শতভাগ পর্যাপ্ত। অতীতের দুঃখ আর ভবিষ্যতের দুশ্চিন্তায় সেই শক্তিকে অপচয় করো না।",
                        textEnglish = "Do not squander the power of patience on the past and future. The past sorrow has evaporated, leaving only spiritual merit. The future suffering does not yet exist. Preserve your strength solely for the present hour.",
                        quranicReference = "সূরা আল-বাকারা ২:১৫৩ - يَا أَيُّهَا الَّذِينَ آمَنُوا اسْتَعِينُوا بِالصَّبْرِ وَالصَّلَاةِ"
                    )
                )
            )
        ),
        "ihlas" to listOf(
            RisaleChapter(
                id = "ihlas_ch_1",
                bookId = "ihlas",
                chapterNumber = 1,
                titleOriginal = "Birinci Düstur: Amelinizde Rıza-yı İlâhî Olmalı",
                titleBengali = "প্রথম নীতি: আমলে শুধুমাত্র আল্লাহর সন্তুষ্টির সন্ধান",
                titleEnglish = "First Rule: Seek Only Divine Pleasure in Actions",
                introductionBengali = "ইসলামী খেদমত ও দাওয়াতের মূল প্রাণ হলো ইখলাস বা নিখাদ আল্লাহর সন্তুষ্টি।",
                sections = listOf(
                    RisaleSection(
                        id = "ihlas_1_1",
                        sectionNumber = 1,
                        subHeadingBengali = "মানুষের প্রশংসা নয়, আল্লাহর সন্তুষ্টিই লক্ষ্য",
                        subHeadingEnglish = "Divine Approval Over Human Acclaim",
                        textOriginal = "Amelinizde rıza-yı İlâhî olmalı. Eğer o razı olsa, bütün dünya küsse ehemmiyeti yok.",
                        textBengali = "তোমাদের প্রতিটি কাজে একমাত্র লক্ষ্য হতে হবে আল্লাহর সন্তুষ্টি। যদি তিনি সন্তুষ্ট হন, তবে গোটা বিশ্বও যদি অসন্তুষ্ট হয় তাতে কিছুই আসে যায় না। আর যদি তিনি অসন্তুষ্ট হন, তবে সারা পৃথিবীর মানুষের প্রশংসাও কোনো উপকারে আসবে না। সুতরাং খ্যাতি, পদমর্যাদা ও লোকদেখানো আকাঙ্ক্ষা ত্যাগ করে কেবল আল্লাহর জন্যই দ্বীনের খেদমতে নিবেদিত হতে হবে।",
                        textEnglish = "Your primary aim in action must be the pleasure of God. If He is pleased, the opposition of the entire world is irrelevant. If He is displeased, worldly acclaim is of no benefit. Therefore, labor exclusively for Divine approval.",
                        quranicReference = "সূরা আল-বায়্যিনাহ ৯৮:৫ - وَمَا أُمِرُوا إِلَّا لِيَعْبُدُوا اللَّهَ مُخْلِصِينَ لَهُ الدِّينَ"
                    )
                )
            ),
            RisaleChapter(
                id = "ihlas_ch_2",
                bookId = "ihlas",
                chapterNumber = 2,
                titleOriginal = "İkinci Düstur: Kardeşlerinizi Tenkit Etmemek",
                titleBengali = "দ্বিতীয় নীতি: দ্বীনি ভাইদের সমালোচনা ও হিংসা থেকে বাঁচা",
                titleEnglish = "Second Rule: Refrain From Criticizing Fellow Believers",
                introductionBengali = "দ্বীনি কর্মে নিয়োজিত ভাইবোনদের মধ্যে হিংসা ও শ্রেষ্ঠত্ব জাহিরের মানসিকতা ইখলাস বিনষ্ট করে।",
                sections = listOf(
                    RisaleSection(
                        id = "ihlas_2_1",
                        sectionNumber = 1,
                        subHeadingBengali = "পরস্পর হাত ও চোখের মতো সহযোগিতা",
                        subHeadingEnglish = "Harmonious Cooperation Like Limbs",
                        textOriginal = "Bu hizmet-i Kur'âniyede bulunan kardeşlerinizi tenkit etmemek ve onların meziyetleriyle iftihar etmektir.",
                        textBengali = "কুরআনের খেদমতে নিয়োজিত তোমার সঙ্গীদের খুঁত ধরা বা তাদের সাথে প্রতিযোগিতায় লিপ্ত না হয়ে, তাদের গুণ ও সফলতা দেখে আনন্দিত হও। যেমন দেহের দুটি হাত কখনোই একে অপরের সাথে যুদ্ধ করে না, তেমনি মুমিনদের পরস্পরের প্রতি সহযোগিতাপূর্ণ হওয়া উচিত।",
                        textEnglish = "In this Qur'anic service, do not criticize your spiritual brethren, but rejoice in their virtues. Just as the right hand does not contend with the left, believers must cooperate as harmonious limbs of one body.",
                        hadithReference = "মুমিনরা একে অপরের জন্য একটি সুদৃঢ় প্রাচীরের মতো, যার একটি অংশ অপর অংশকে শক্তি জোগায়। (সহিহ বুখারি)"
                    )
                )
            )
        ),
        "genclik" to listOf(
            RisaleChapter(
                id = "genclik_ch_1",
                bookId = "genclik",
                chapterNumber = 1,
                titleOriginal = "Gençlik Nimetinin Şükrü",
                titleBengali = "যৌবনের অমূল্য নেয়ামত ও তার সঠিক মূল্যায়ন",
                titleEnglish = "Gratitude for the Blessing of Youth",
                introductionBengali = "তারুণ্য হলো মানবজীবনের সবচেয়ে প্রদীপ্ত কাল, যা ক্ষণস্থায়ী কিন্তু চিরন্তন ভবিষ্যতের ভাগ্য নির্ধারণ করে।",
                sections = listOf(
                    RisaleSection(
                        id = "genclik_1_1",
                        sectionNumber = 1,
                        subHeadingBengali = "ক্ষণিক সুখ বনাম অনন্ত আফসোস",
                        subHeadingEnglish = "Fleeting Pleasure vs. Eternal Regret",
                        textOriginal = "Gençlik hiç şüphe yok ki gidecek. Yaz güze ve kışa yer vermesi gibi, gençlik dahi ihtiyarlığa ve ölüme tebdil olacak.",
                        textBengali = "নিঃসন্দেহে যৌবন একদিন বিদায় নেবে। গ্রীষ্মকাল যেমন শীতের আগমন ঘটায়, তেমনি তারুণ্যও একসময় বার্ধক্য ও মৃত্যুর মুখোমুখি হবে। যদি একজন যুবক তার এই শক্তি আল্লাহর আনুগত্য ও পবিত্র চরিত্রে ব্যয় করে, তবে সেই যৌবন আখিরাতে চিরসবুজ অমলিন জান্নাত হয়ে ফিরে আসবে। আর যদি অবাধ্যতায় অপচয় করে, তবে তার সামান্যতম পাপবোধও অন্তরে বিষাক্ত কাঁটার মতো কষ্ট দেবে।",
                        textEnglish = "Without doubt, youth will depart. Just as summer yields to autumn and winter, youth inevitably transitions into old age and mortality. If channeled in virtuous obedience, it blossoms into immortal youth in Paradise.",
                        hadithReference = "বার্ধক্য আসার পূর্বে যৌবনকে মূল্যায়ন করো। (মুসতাদরাক হাকিম)"
                    )
                )
            )
        )
    )

    // Contextual Islamic & Ottoman Lügat (Dictionary)
    val lugatWords: List<LugatWord> = listOf(
        LugatWord(
            id = "w_tawhid",
            term = "Tevhid",
            pronunciationBengali = "তাওহিদ",
            pronunciationEnglish = "Tawhid",
            originLanguage = "আরবি",
            meaningBengali = "আল্লাহর একত্ববাদ। সৃষ্টিজগতের প্রতিটি কণা ও ঘটনার একমাত্র অধিপতি ও স্রষ্টা আল্লাহ তায়ালা।",
            meaningEnglish = "The absolute oneness and uniqueness of Allah; the indivisible divine unity governing the universe.",
            rootOrigin = "و-ح-د (ওয়াহাদা)",
            theologicalContextBengali = "রিসালে-ই নূরের প্রধান আলোচ্য বিষয়। বিশ্বপ্রকৃতির কোনো কিছুই স্বয়ংক্রিয় নয়, বরং আল্লাহর প্রত্যক্ষ সৃষ্টি ও ইচ্ছা।",
            sampleUsage = "লা ইলাহা ইল্লাল্লাহ - তাওহিদের সর্বোচ্চ ঘোষণা।"
        ),
        LugatWord(
            id = "w_hashir",
            term = "Haşir",
            pronunciationBengali = "হাশর ও পরকাল",
            pronunciationEnglish = "Hashir",
            originLanguage = "আরবি",
            meaningBengali = "মৃত্যুর পর পুনরুত্থান এবং বিচারদিবসে সমস্ত সৃষ্টির একত্র সমাবেশ।",
            meaningEnglish = "Resurrection after death and the gathering of all created beings on the Day of Judgment.",
            rootOrigin = "ح-ش-ر (হাশারা)",
            theologicalContextBengali = "দশম বাণীতে (The 10th Word) বদিউজ্জামান সাঈদ নুরসী যুক্তি ও প্রকৃতির পুনর্জাগরণ দিয়ে হাশর প্রমাণ করেছেন।",
            sampleUsage = "বসন্তকালে মৃত মাটির পুনর্জীবন যেমন সত্য, তেমনি মানুষের হাশরও সুনিশ্চিত সত্য।"
        ),
        LugatWord(
            id = "w_nubuwwah",
            term = "Nübüvvet",
            pronunciationBengali = "নবুওয়াত / রিসালাত",
            pronunciationEnglish = "Nubuwwah",
            originLanguage = "আরবি",
            meaningBengali = "আল্লাহর পক্ষ থেকে প্রেরিত পয়গাম্বরদের দায়িত্ব ও শিক্ষা প্রচারের বিধান।",
            meaningEnglish = "Prophethood; the divine institution of messengers transmitting guidance to humanity.",
            rootOrigin = "ن-ب-أ (নাবা')",
            theologicalContextBengali = "হযরত মুহাম্মদ (সা.) হলেন নবুওয়াতের মোহর ও পূর্ণতা।"
        ),
        LugatWord(
            id = "w_adalet",
            term = "Adalet",
            pronunciationBengali = "আদালত / ইনসাফ",
            pronunciationEnglish = "Adalah",
            originLanguage = "আরবি",
            meaningBengali = "পরম ন্যায়বিচার ও ভারসাম্য। প্রতিটি বস্তুকে তার উপযুক্ত স্থানে স্থাপন করা।",
            meaningEnglish = "Absolute justice and divine balance throughout creation.",
            rootOrigin = "ع-د-ل (আদালা)",
            theologicalContextBengali = "মহাবিশ্বের নিখুঁত সুষম ওজন ও পরমাণুর ভারসাম্য আল্লাহর 'আল-আদল' নামের প্রতিফলন।"
        ),
        LugatWord(
            id = "w_ubudiyyet",
            term = "Ubudiyet",
            pronunciationBengali = "উবুদিয়্যত / দাসত্ব",
            pronunciationEnglish = "Ubudiyyah",
            originLanguage = "আরবি",
            meaningBengali = "আল্লাহর সামনে চরম বিনয় ও বান্দাসুলভ আনুগত্য প্রদর্শন।",
            meaningEnglish = "Servanthood; sincere humility and submission of the creature before the Supreme Creator.",
            rootOrigin = "ع-ب-د (আবাদা)",
            theologicalContextBengali = "আল্লাহর রুবুবিয়াতের (প্রভুত্বের) প্রতি বান্দার একমাত্র স্বাভাবিক প্রতিক্রিয়া হলো উবুদিয়াত।"
        ),
        LugatWord(
            id = "w_rububiyyet",
            term = "Rububiyet",
            pronunciationBengali = "রুবুবিয়্যাত / প্রতিপালন",
            pronunciationEnglish = "Rububiyyah",
            originLanguage = "আরবি",
            meaningBengali = "আল্লাহর সর্বময় কর্তৃত্ব, লালন-পালন ও প্রতিটি অণু-পরমাণুর পরিচালনার ক্ষমতা।",
            meaningEnglish = "Sovereign Lordship; Allah's continuous nurturing, sustaining, and governance of all realms.",
            rootOrigin = "ر-ب-ب (রাব্বা)"
        ),
        LugatWord(
            id = "w_ihlas",
            term = "İhlas",
            pronunciationBengali = "ইখলাস / নিষ্ঠা",
            pronunciationEnglish = "Ikhlas",
            originLanguage = "আরবি",
            meaningBengali = "সকল প্রকার লোকদেখানো ও দুনিয়াবি স্বার্থমুক্ত হয়ে কেবল আল্লাহর সন্তুষ্টির জন্য কাজ করা।",
            meaningEnglish = "Absolute sincerity of intent; performing acts solely for God's goodwill without worldly motive.",
            rootOrigin = "خ-ل-ص (খালাসা)"
        ),
        LugatWord(
            id = "w_kader",
            term = "Kader",
            pronunciationBengali = "ক্বদর / তাকদির",
            pronunciationEnglish = "Qadar",
            originLanguage = "আরবি",
            meaningBengali = "আল্লাহর চিরন্তন জ্ঞান অনুসারে সকল সৃষ্টির পরিমাপ, ভাগ্য ও নির্ধারিত বিধান।",
            meaningEnglish = "Divine destiny and measurement; Allah's eternal decree and knowledge encompassing all things.",
            rootOrigin = "ق-د-ر (ক্বাদারা)"
        ),
        LugatWord(
            id = "w_sunnetullah",
            term = "Sünnetullah",
            pronunciationBengali = "সুন্নাতুল্লাহ",
            pronunciationEnglish = "Sunnatullah",
            originLanguage = "আরবি",
            meaningBengali = "সৃষ্টিজগতে আল্লাহর নির্ধারিত প্রাকৃতিক ও নৈতিক অপরিবর্তনীয় নিয়মাবলী।",
            meaningEnglish = "The universal way and immutable laws ordained by Allah across creation.",
            rootOrigin = "س-ن-ن (সান্না)"
        ),
        LugatWord(
            id = "w_marifetullah",
            term = "Mârifetullah",
            pronunciationBengali = "মারেফাতুল্লাহ",
            pronunciationEnglish = "Ma'rifatullah",
            originLanguage = "আরবি",
            meaningBengali = "সৃষ্টিজগতের নিদর্শনের মাধ্যমে আল্লাহ তায়ালার নাম ও গুণাবলী সম্পর্কে গভীর জ্ঞান ও উপলব্ধি।",
            meaningEnglish = "Profound spiritual knowledge and experiential awareness of Allah through His divine names and signs.",
            rootOrigin = "ع-ر-ف (আরাফা)"
        ),
        LugatWord(
            id = "w_basiret",
            term = "Basiret",
            pronunciationBengali = "বাসিরাত / অন্তর্দৃষ্টি",
            pronunciationEnglish = "Basirah",
            originLanguage = "আরবি",
            meaningBengali = "ঈমানদীপ্ত আত্মার অন্তর্চক্ষু যা বাহ্যিক আবরণের আড়ালে পরম সত্য প্রত্যক্ষ করে।",
            meaningEnglish = "Spiritual insight and discernment allowing the believer to perceive transcendent truth."
        ),
        LugatWord(
            id = "w_tefekkur",
            term = "Tefekkür",
            pronunciationBengali = "তাফাক্কুর / গভীর চিন্তা",
            pronunciationEnglish = "Tafakkur",
            originLanguage = "আরবি",
            meaningBengali = "আল্লাহর অপরূপ সৃষ্টির বৈচিত্র্য ও কুদরতের প্রতি লক্ষ্য করে কৃতজ্ঞচিত্তে ধ্যান ও মননশীলতা।",
            meaningEnglish = "Deep contemplation of the marvels of creation to deepen gratitude and recognition of God."
        )
    )

    // Thematic Intellectual & Faith Q&A (Addressing contemporary doubts)
    val thematicQuestions: List<RisaleThematicQuestion> = listOf(
        RisaleThematicQuestion(
            id = "qa_1",
            topicBengali = "সৃষ্টি ও ঈশ্বরতত্ত্ব",
            topicEnglish = "Creation & Divinity",
            questionBengali = "সৃষ্টিজগতে এত বৈচিত্র্য ও নিয়ম শৃঙ্খলা কীভাবে একক স্রষ্টার অস্তিত্ব প্রমাণ করে?",
            questionEnglish = "How do the intricate laws and diversity in nature prove the existence of One Supreme Creator?",
            answerSummaryBengali = "একটি সুসজ্জিত প্রাসাদে প্রত্যেকটি আসবাব যেমন একজন প্রকৌশলীর প্রজ্ঞা প্রমাণ করে, তেমনি পরমাণু থেকে গ্যালাক্সি পর্যন্ত সমস্ত কিছুর ঐকতান একক স্রষ্টাকে নির্দেশ করে।",
            answerFullBengali = "রিসালে-ই নূরের 'বাণীসমূহ' গ্রন্থে বদিউজ্জামান দেখিয়েছেন যে, যদি কোনো বইয়ের প্রতিটি অক্ষর একটি জীবন্ত অর্থ প্রকাশ করে, তবে কোনো বিবেকবান মানুষ বলতে পারে না যে কালির ফোঁটা নিজে নিজেই বই হয়ে গেছে। মহাবিশ্বের প্রতিটি ফুল, প্রতিটি মানবদেহ এবং প্রতিটি নক্ষত্র কোটি কোটি নিয়মে বাঁধা। যদি সবকিছু একক স্রষ্টার কর্তৃত্বাধীন না হতো, তবে একাধিক শক্তির দ্বন্দ্বে সৃষ্টিজগত এক মুহূর্তও টিকতে পারত না। অতএব একত্ববাদই সৃষ্টির একমাত্র যৌক্তিক ব্যাখ্যা।",
            answerFullEnglish = "If every letter of a sublime poem conveys wisdom, no rational observer claims the ink spilled accidentally. The cosmos, from biological DNA to celestial galaxies, functions under unified mathematical balance, indicating a Single All-Wise Author.",
            sourceBookReference = "সোজলের (বাণীসমূহ) - ২২তম ও ৩৩তম বাণী",
            keyQuranVerses = listOf("সূরা আল-আনবিয়া ২১:২২ - لَوْ كَانَ فِيهِمَا آلِهَةٌ إِلَّا اللَّهُ لَفَسَدَتَا", "সূরা আল-মুলক ৬৭:৩ - مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَٰنِ مِن تَفَاوُتٍ")
        ),
        RisaleThematicQuestion(
            id = "qa_2",
            topicBengali = "দুঃখ-কষ্ট ও ইলাহী হিকমত",
            topicEnglish = "Suffering & Divine Wisdom",
            questionBengali = "আল্লাহ কেন পৃথিবীতে রোগ, শোক ও কষ্ট সৃষ্টি করেছেন?",
            questionEnglish = "Why does God permit illness, earthquakes, and suffering in the world?",
            answerSummaryBengali = "কষ্ট না থাকলে সুখের প্রকৃত মূল্য বোঝা যায় না; দুঃখ মানুষের ঘুমন্ত গুণাবলী জাগ্রত করে এবং আত্মিক পরিশুদ্ধি ঘটায়।",
            answerFullBengali = "রিসালে-ই নূরের দর্শন অনুযায়ী, অন্ধকার না থাকলে আলোর অস্তিত্ব বোঝা যেত না; ক্ষুধা না থাকলে খাদ্যের স্বাদ উপলব্ধি হতো না। একইভাবে রোগ ও বিপদ মানুষের অন্তরে অহংকার চূর্ণ করে, পরকালের চিরস্থায়ী জীবনের প্রস্তুতি নিশ্চিত করে এবং সহমর্মিতার বিকাশ ঘটায়। স্বর্ণ যেমন আগুনে পুড়ে খাঁটি হয়, মানুষও প্রতিকূলতার মধ্য দিয়ে খাঁটি মুমিনে পরিণত হয়।",
            answerFullEnglish = "Light is known through darkness; health is appreciated through illness. Tribulations dismantle human hubris, elevate spiritual ranks, and foster profound compassion and reliance upon the Divine.",
            sourceBookReference = "হাসতালার রিসালেসি (অসুস্থদের সান্ত্বনা) ও ২য় লেমা",
            keyQuranVerses = listOf("সূরা আল-বাকারা ২:১৫৫ - وَلَنَبْلُوَنَّكُم بِشَيْءٍ مِّنَ الْخَوْفِ وَالْجُوعِ")
        ),
        RisaleThematicQuestion(
            id = "qa_3",
            topicBengali = "পরকাল ও পুনরুত্থান",
            topicEnglish = "Afterlife & Resurrection",
            questionBengali = "মৃত্যুর পর কোটি কোটি বছর মাটির সাথে মিশে যাওয়ার পর মানুষ কীভাবে পুনরায় জীবিত হবে?",
            questionEnglish = "How can humans be resurrected physically after bodies decompose into dust?",
            answerSummaryBengali = "যিনি শীতের মরা মাটিকে প্রতি বসন্তে লক্ষ লক্ষ জীবন্ত লতাপাতা দিয়ে জাগিয়ে তোলেন, তাঁর পক্ষে মানুষের পুনরুত্থান ঘটানো আরও সহজ।",
            answerFullBengali = "দশম বাণীতে (The 10th Word) চমৎকার দৃষ্টান্ত দেওয়া হয়েছে: প্রতিটি শীতকালে পৃথিবী যেন একটি কবরস্থানে পরিণত হয়। কিন্তু বসন্ত আসতেই একটিমাত্র আদেশে কোটি কোটি গাছপালা, পতঙ্গ ও বৃক্ষরাজির পুনরুজ্জীবন ঘটে ঠিক আগের আকৃতিতেই। যিনি প্রথমবার মানবজাতিকে অনস্তিত্ব থেকে সৃষ্টি করেছেন, তাঁর পক্ষে দ্বিতীয়বার একত্র করা কোনো কঠিন কাজ নয়। এ ছাড়া আল্লাহর নিখুঁত ন্যায়বিচার দাবি করে যে, পৃথিবীতে অত্যাচারী ও মজলুমের চূড়ান্ত ফয়সালার জন্য একটি পরকাল অবশ্যই আবশ্যক।",
            answerFullEnglish = "He who revives dormant seeds each spring under the unified command of regeneration can effortlessly assemble the disintegrated physical forms of mankind for the final accountability.",
            sourceBookReference = "সোজলের (বাণীসমূহ) - ১০ম বাণী (হাশর রিসালেসি)",
            keyQuranVerses = listOf("সূরা ইয়াসিন ৩৬:৭৮-৭৯ - قُلْ يُحْيِيهَا الَّذِي أَنشَأَهَا أَوَّلَ مَرَّةٍ")
        ),
        RisaleThematicQuestion(
            id = "qa_4",
            topicBengali = "ভাগ্য ও মানুষের স্বাধীনতা",
            topicEnglish = "Free Will and Destiny",
            questionBengali = "সবকিছু যদি তাকদিরে আগে থেকেই লেখা থাকে, তবে মানুষের পাপ বা পুণ্যের দায় কার?",
            questionEnglish = "If all events are pre-recorded in Destiny (Qadar), why are humans accountable for their choices?",
            answerSummaryBengali = "আল্লাহর জ্ঞান হলো ভবিষ্যৎদর্শী ও অনন্ত; আল্লাহ জানেন বলেই মানুষ কাজ করে না, বরং মানুষ স্বাধীন ইচ্ছায় কাজটি করবে বলেই তা আগে থেকেই জানা।",
            answerFullBengali = "রিসালে-ই নূরে তাকদিরের সূক্ষ্ম ভারসাম্য ব্যাখ্যাকারী ২৬তম বাণীতে বলা হয়েছে: একজন দূরদর্শী পথপ্রদর্শক যদি পাহাড়ের ওপর দাঁড়িয়ে দেখেন যে সামনের বাঁকে দুটি ট্রেন একে অপরের দিকে এগিয়ে আসছে এবং ডায়েরিতে লিখে রাখেন যে তারা সংঘর্ষে লিপ্ত হবে, তবে তার জানার কারণে দুর্ঘটনা ঘটেনি, বরং ট্রেনের চালকদের অসতর্কতার কারণেই ঘটেছে। মানুষের 'জুযয়ী ইরাদা' বা ক্ষুদ্র ইচ্ছাশক্তি রয়েছে যার ভিত্তিতে সে ভালো বা মন্দ বেছে নেয়। আল্লাহ তার কর্মের পথ খুলে দেন, তাই দায় মানুষের নিজের।",
            answerFullEnglish = "Divine knowledge encompasses past, present, and future unconditionally. Destiny's record reflects God's foreknowledge of our autonomous moral choices; knowledge is descriptive, not coercive.",
            sourceBookReference = "সোজলের - ২৬তম বাণী (ক্বদর রিসালেসি)",
            keyQuranVerses = listOf("সূরা আল-কাহফ ১৮:২৯ - فَمَن شَاءَ فَلْيُؤْمِن وَمَن شَاءَ فَلْيَكْفُرْ")
        )
    )

    // Aphorisms & Wisdom Cards (Vecizeler)
    val wisdomQuotes: List<WisdomQuote> = listOf(
        WisdomQuote(
            id = "q_1",
            textBengali = "ঈমান কেবল একটি ধারণা নয়; ঈমান হলো জ্যোতি এবং ঈমান হলো শক্তি। যে খাঁটি ঈমান ধারণ করে, সে সমগ্র মহাবিশ্বকে জয় করতে পারে।",
            textEnglish = "Faith is both a light and a strength. One who attains true faith can challenge the entire universe.",
            textOriginal = "İman hem nurdur, hem kuvvettir. Hakiki imanı elde eden adam, kâinata meydan okuyabilir.",
            sourceBook = "বাণীসমূহ (সোজলের)",
            category = "ঈমান"
        ),
        WisdomQuote(
            id = "q_2",
            textBengali = "মৃত্যু কোনো বিনাশ বা অনন্ত শূন্যতা নয়; বরং তা ক্ষণস্থায়ী জেলখানা থেকে চিরন্তন আনন্দের রাজপ্রাসাদে স্থানান্তরের এক রূপালী তোরণ।",
            textEnglish = "Death is not annihilation; it is a release from worldly servitude into the palace of eternal felicity.",
            textOriginal = "Ölüm idam-ı ebedî değil, belki vazifeden bir terhistir.",
            sourceBook = "চিঠিপত্র (মেকতুবাাত)",
            category = "পরকাল"
        ),
        WisdomQuote(
            id = "q_3",
            textBengali = "হে অসুস্থ ব্যক্তি! নিরাশ হয়ো না। তোমার প্রতিটি মুহূর্তের ধৈর্য একটি রত্নের মতো সওয়াবে রূপান্তরিত হচ্ছে।",
            textEnglish = "O ailing friend! Despair not. Every breath endured with patience is transformed into spiritual gems.",
            textOriginal = "Ey hasta! Senin hastalığın sana dert değil, belki bir nevi dermandır.",
            sourceBook = "অসুস্থদের সান্ত্বনা",
            category = "ধৈর্য"
        ),
        WisdomQuote(
            id = "q_4",
            textBengali = "দ্বীনি কাজে মানুষের বাহবা বা সংখ্যাধিক্যের লোভ করোনা; যদি আল্লাহ সন্তুষ্ট হন, তবে গোটা সৃষ্টি তোমার পক্ষে স্বাক্ষী দেবে।",
            textEnglish = "Do not seek praise or sheer numbers in sacred service; if God is pleased, the whole cosmos will stand as witness.",
            textOriginal = "Amelinizde rıza-yı İlâhî olmalı.",
            sourceBook = "ইখলাস পুস্তিকা",
            category = "ইখলাস"
        ),
        WisdomQuote(
            id = "q_5",
            textBengali = "তারুণ্য হলো বসন্তকালের মতো উজ্জ্বল, তবে দ্রুতগামী। একে আল্লাহর আনুগত্যে পরিচালিত করো, তবে তা আখিরাতে অনন্ত যৌবন হয়ে ফিরবে।",
            textEnglish = "Youth is bright like spring yet swift to pass. Direct it towards divine devotion, and it will return as immortal radiance in the Hereafter.",
            textOriginal = "Gençlik hiç şüphe yok ki gidecek.",
            sourceBook = "যুবকদের পথনির্দেশিকা",
            category = "যুবসমাজ"
        )
    )

    private val _readingGoal = MutableStateFlow(RisaleReadingGoal())
    val readingGoal: StateFlow<RisaleReadingGoal> = _readingGoal.asStateFlow()

    fun getBook(bookId: String): RisaleBook? {
        return books.find { it.id == bookId }
    }

    fun getChapters(bookId: String): List<RisaleChapter> {
        return chaptersMap[bookId] ?: emptyList()
    }

    fun getChapter(bookId: String, chapterId: String): RisaleChapter? {
        return chaptersMap[bookId]?.find { it.id == chapterId }
    }

    fun searchLugat(query: String): List<LugatWord> {
        if (query.isBlank()) return lugatWords
        val q = query.trim().lowercase()
        return lugatWords.filter {
            it.term.lowercase().contains(q) ||
            it.pronunciationBengali.contains(q) ||
            it.meaningBengali.contains(q) ||
            it.meaningEnglish.lowercase().contains(q)
        }
    }

    fun searchBooksAndChapters(query: String): List<Pair<RisaleBook, RisaleChapter>> {
        if (query.isBlank()) return emptyList()
        val q = query.trim().lowercase()
        val results = mutableListOf<Pair<RisaleBook, RisaleChapter>>()
        for (book in books) {
            val chs = chaptersMap[book.id] ?: continue
            for (ch in chs) {
                if (ch.titleBengali.lowercase().contains(q) ||
                    ch.titleEnglish.lowercase().contains(q) ||
                    ch.sections.any { it.textBengali.contains(q) || it.textEnglish.lowercase().contains(q) }) {
                    results.add(book to ch)
                }
            }
        }
        return results
    }

    fun updateReadingProgress(bookId: String, chapterId: String, pagesReadDelta: Int = 1) {
        val current = _readingGoal.value
        val book = getBook(bookId)
        val chapter = getChapter(bookId, chapterId)
        _readingGoal.value = current.copy(
            totalPagesRead = current.totalPagesRead + pagesReadDelta,
            lastReadBookId = bookId,
            lastReadChapterId = chapterId,
            lastReadChapterTitle = "${book?.titleBengali ?: ""} - ${chapter?.titleBengali ?: ""}"
        )
    }
}
