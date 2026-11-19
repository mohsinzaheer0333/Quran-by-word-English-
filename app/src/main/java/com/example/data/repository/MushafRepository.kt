package com.example.data.repository

import android.content.Context
import com.example.model.MushafEditionData
import com.example.model.MushafLine
import com.example.model.MushafLineType
import com.example.model.MushafPageData
import com.example.model.MushafScript
import com.example.model.MushafToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.ConcurrentHashMap

class MushafRepository(private val context: Context) {

  private val cache = ConcurrentHashMap<MushafScript, MushafEditionData>()

  suspend fun getEdition(script: MushafScript): MushafEditionData {
    cache[script]?.let { return it }

    return withContext(Dispatchers.IO) {
      val assetPath = when (script) {
        MushafScript.INDOPAK -> "mushaf/indopak_16.json"
        MushafScript.UTHMANI -> "mushaf/madinah_15.json"
      }

      val jsonText = context.assets.open(assetPath).use { stream ->
        BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).readText()
      }

      val root = JSONObject(jsonText)
      val linesPerPage = root.getInt("linesPerPage")
      val totalPages = root.getInt("totalPages")

      val surahPagesObj = root.getJSONObject("surahPages")
      val surahFirstPage = HashMap<Int, Int>(114)
      surahPagesObj.keys().forEach { key ->
        key.toIntOrNull()?.let { surahNum ->
          surahFirstPage[surahNum] = surahPagesObj.getInt(key)
        }
      }

      val juzPagesObj = root.getJSONObject("juzPages")
      val juzFirstPage = HashMap<Int, Int>(30)
      juzPagesObj.keys().forEach { key ->
        key.toIntOrNull()?.let { juzNum ->
          juzFirstPage[juzNum] = juzPagesObj.getInt(key)
        }
      }

      val pagesArray = root.getJSONArray("pages")
      val pages = ArrayList<MushafPageData>(pagesArray.length())

      for (i in 0 until pagesArray.length()) {
        val pObj = pagesArray.getJSONObject(i)
        val pageNum = pObj.getInt("p")
        val juzNum = pObj.getInt("j")
        val surahNum = pObj.getInt("s")

        val linesArray = pObj.getJSONArray("lines")
        val parsedByNum = HashMap<Int, MushafLine>(linesArray.length())
        for (l in 0 until linesArray.length()) {
          val lObj = linesArray.getJSONObject(l)
          val lineNum = lObj.getInt("n")
          val typeCode = lObj.getString("t")
          val line = when (typeCode) {
            "s" -> MushafLine(
              lineNumber = lineNum,
              type = MushafLineType.SURAH_HEADER,
              surahNumber = lObj.optInt("ch", surahNum)
            )
            "b" -> MushafLine(
              lineNumber = lineNum,
              type = MushafLineType.BISMILLAH,
              surahNumber = lObj.optInt("ch", surahNum),
              bannerSurahNumber = lObj.optInt("bannerCh", 0)
            )
            else -> {
              val toksArr = lObj.optJSONArray("toks")
              val tokens = if (toksArr != null) {
                ArrayList<MushafToken>(toksArr.length()).apply {
                  for (t in 0 until toksArr.length()) {
                    val raw = toksArr.getString(t)
                    if (raw.startsWith("E:")) {
                      val ayahNum = raw.substring(2).toIntOrNull() ?: 1
                      add(MushafToken.AyahEnd(ayahNum))
                    } else if (raw.isNotEmpty()) {
                      add(MushafToken.Word(raw))
                    }
                  }
                }
              } else {
                emptyList()
              }
              MushafLine(
                lineNumber = lineNum,
                type = MushafLineType.TEXT,
                tokens = tokens,
                hasRukuEnd = lObj.optBoolean("r", false)
              )
            }
          }
          parsedByNum[lineNum] = line
        }

        // Normalize strictly to exact linesPerPage slots (1..16 for Indo-Pak, 1..15 for Madinah)
        val normalizedLines = ArrayList<MushafLine>(linesPerPage)
        for (slot in 1..linesPerPage) {
          normalizedLines.add(
            parsedByNum[slot] ?: MushafLine(lineNumber = slot, type = MushafLineType.EMPTY)
          )
        }

        pages.add(
          MushafPageData(
            pageNumber = pageNum,
            juzNumber = juzNum,
            primarySurahNumber = surahNum,
            lines = normalizedLines
          )
        )
      }

      val edition = MushafEditionData(
        script = script,
        linesPerPage = linesPerPage,
        totalPages = totalPages,
        surahFirstPage = surahFirstPage,
        juzFirstPage = juzFirstPage,
        pages = pages
      )
      cache[script] = edition
      edition
    }
  }
}
