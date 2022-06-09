/* Copyright (c) 2021 - 2022 Buijs Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package dev.buijs.klutter.core.tasks


import dev.buijs.klutter.core.*
import dev.buijs.klutter.core.shared.*

/**
 * Task to generate the boilerplate code required to let Kotlin Multiplatform and Flutter communicate.
 */
class AdapterGeneratorTask(
    private val android: Android,
    private val ios: IOS,
    private val root: Root,
    private val platform: Platform,
) : KlutterTask {

    override fun run() {
        AdapterCollector(root, platform).data.let { data ->
            FlutterAdapterGenerator(root, data).generate()
            AndroidAdapter(android, data).generate()
            IosAdapterGenerator(ios, data).generate()
        }
    }

}