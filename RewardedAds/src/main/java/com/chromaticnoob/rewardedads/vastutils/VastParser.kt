package com.chromaticnoob.rewardedads.vastutils

import org.jdom2.Element
import org.jdom2.input.JDOMParseException
import org.jdom2.input.SAXBuilder
import java.io.ByteArrayInputStream

abstract class VastParser {
    companion object {
        /**
         *               Only supports VAST 2.0 for now !
         * VAST-Specific primitive variables which will be returned later.
         */

        private var vastVersion = "nyd" //nyd=notyetdetermined
        private var shortAdId: String = "nyd"
        private var adType: String = "nyd"
        private var adSystemName = "nyd"
        private var adSystemVersion = "nyd"
        private var adTitle = "nyd"
        private var error = "nyd"
        private var impressions = mutableListOf<String>()
        private var creativeLIST = mutableListOf<Creative>()
        private var parsedVastObject = VastObject()


        /**
         * Parse-related variables
         */
        private lateinit var creatives: MutableList<Element>

        fun parseXML(vast: String): VastObject {
            //Preparing our XML parser.
            val sax = SAXBuilder()
            sax.setFeature("http://xml.org/sax/features/external-general-entities", false)
            sax.expandEntities = false
            try {
                val doc = sax.build(ByteArrayInputStream(vast.toByteArray()))
                //Getting nodes one by one
                val parentVast = doc.rootElement
                vastVersion = doc.rootElement.getAttribute("version").value
                val adElement = parentVast.getChild("Ad")
                shortAdId = adElement.getAttribute("id").value
                val majorElement = adElement.children[0]
                adType = majorElement.name //Getting the InLine ad type.

                /* Now we start getting the first non-creative nodes one by one */
                val childnodes = majorElement.children
                for (node in childnodes) {
                    val nodeName = node.name
                    when (nodeName) {
                        "AdSystem" -> {
                            adSystemName = node.value
                            adSystemVersion = if (node.attributesSize != 0) {
                                node.getAttribute("version").value
                            } else {
                                ""
                            }
                        }
                        "AdTitle" -> {
                            adTitle = node.value
                        }
                        "Impression" -> {
                            impressions.add(node.value)
                        }
                        "Error" -> {
                            error = node.value
                        }
                        "Creatives" -> {
                            creatives = node.children
                        }
                    }
                }
                //Getting Creatives as a separate cascade and storing them in a map according to their sequence
                for (creative in creatives) {
                    val theCreative = Creative() //Our custom creative class
                    val attr = creative.attributes
                    theCreative.sequence = if (null != creative.getAttribute("sequence")) {
                        creative.getAttribute("sequence").value
                    } else {
                        "1"
                    }
                    theCreative.creativeid = creative.getAttributeValue("id")
                    theCreative.creativeadId = creative.getAttributeValue("adId")

                    val creativecontent = creative.children
                    for (cc in creativecontent) {
                        when (cc.name) {
                            "UniversalAdId" -> {
                                theCreative.universalid = cc.value
                            }
                            "Linear" -> {
                                val linearchildren = cc.children
                                for (child in linearchildren) {
                                    when (child.name) {
                                        "Duration" -> {
                                            theCreative.duration = child.value
                                        }
                                        "TrackingEvents" -> {
                                            if (child.children.isNotEmpty()) {
                                                val eventMap = mutableMapOf<String, String>()
                                                val events = child.children
                                                for (event in events) {
                                                    eventMap[event.getAttributeValue("event")] =
                                                        event.value
                                                }
                                                theCreative.trackingEvents = eventMap
                                            }
                                        }
                                        "VideoClicks" -> {
                                            if (child.children.isNotEmpty()) {
                                                val clicks = child.children
                                                for (clickthing in clicks) {
                                                    when (clickthing.name) {
                                                        "ClickTracking" -> {
                                                            theCreative.videoClickTracking =
                                                                clickthing.value
                                                        }
                                                        "ClickThrough" -> {
                                                            theCreative.videoClickThrough =
                                                                clickthing.value
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        "MediaFiles" -> {
                                            if (child.children.isNotEmpty()) {
                                                val medias = child.children
                                                for (media in medias) {
                                                    val mediaFile = MediaFile()
                                                    mediaFile.url = media.value
                                                    for (attribute in media.attributes)
                                                        when (attribute.name) {
                                                            "bitrate" -> {
                                                                mediaFile.bitrate =
                                                                    attribute.value.toIntOrNull()
                                                            }
                                                            "height" -> {
                                                                mediaFile.height =
                                                                    attribute.value.toIntOrNull()
                                                            }
                                                            "width" -> {
                                                                mediaFile.width =
                                                                    attribute.value.toIntOrNull()
                                                            }
                                                            "type" -> {
                                                                mediaFile.mimetype = attribute.value
                                                            }
                                                            "maintainAspectRatio" -> {
                                                                mediaFile.maintianAspectRatio =
                                                                    attribute.value.toBooleanStrictOrNull() == true
                                                            }
                                                        }
                                                    theCreative.mediaList.add(mediaFile)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    creativeLIST.add(theCreative)
                }

                parsedVastObject.vastVersion = vastVersion
                parsedVastObject.shortAdId = shortAdId
                parsedVastObject.adType = adType
                parsedVastObject.adSystem = adSystemName
                parsedVastObject.adSystemVersion = adSystemVersion
                parsedVastObject.impressionList = impressions
                parsedVastObject.adTitle = adTitle
                parsedVastObject.creativeList = creativeLIST
                parsedVastObject.error = error

            } catch (jdomE: JDOMParseException) {
            }
            return parsedVastObject
        }

    }
}