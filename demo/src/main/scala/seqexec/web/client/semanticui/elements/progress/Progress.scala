// Copyright (c) 2016-2019 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package seqexec.web.client.semanticui.elements.progress

import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.querki.jquery.$
import react.common._
import react.common.implicits._
import org.querki.jquery.JQuery
import org.querki.jsext.JSOptionBuilder
import org.querki.jsext._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport


object SemanticUIProgress {

  @js.native
  @JSImport("semantic-ui-progress", JSImport.Namespace)
  private object SemanticProgressModule extends js.Any

  SemanticProgressModule

  @js.native
  trait JsProgressOptions extends js.Object

  object JsProgressOptions extends JsProgressOptionBuilder(noOpts)

  class JsProgressOptionBuilder(val dict: OptMap)
    extends JSOptionBuilder[JsProgressOptions, JsProgressOptionBuilder](
      new JsProgressOptionBuilder(_)) {
    def total(v:     Long): JsProgressOptionBuilder    = jsOpt("total", v)
    def value(v:     Long): JsProgressOptionBuilder    = jsOpt("value", v)
    def percent(v:   Double): JsProgressOptionBuilder  = jsOpt("percent", v)
    def debug(v:     Boolean): JsProgressOptionBuilder = jsOpt("debug", v)
    def precision(v: Int): JsProgressOptionBuilder     = jsOpt("precision", v)
    def onChange[A](
                     t: js.Function3[js.Any, js.Any, js.Any, A]): JsProgressOptionBuilder =
      jsOpt("onChange", t)
  }

  @js.native
  trait SemanticProgress extends JQuery {
    def progress(o: JsProgressOptions): this.type
  }

  implicit def jq2Semantic($ : JQuery): SemanticProgress =
    $.asInstanceOf[SemanticProgress]

}

import SemanticUIProgress._

/**
  * Produces a progress element using javascript
  */
object Progress {
  final case class Props(label:       String,
                         total:       Long,
                         value:       Long,
                         indicating:  Boolean = false,
                         progress:    Boolean = false,
                         color:       Option[String] = None,
                         progressCls: List[Css] = Nil,
                         barCls:      List[Css],
                         labelCls:    List[Css] = Nil)

  private val component = ScalaComponent
    .builder[Props]("Progress")
    .stateless
    .renderPC( (_, p, c) =>
      <.div(
        ^.cls := "ui progress",
        p.color.map(u => ^.cls := u).whenDefined,
        ^.classSet(
          "indicating" -> p.indicating
        ),
        p.progressCls,
        <.div(^.cls := "bar",
              p.barCls,
              <.div(^.cls := "progress").when(p.progress)),
        <.div(^.cls := "label",
              p.labelCls,
              p.label),
        c
      )
    )
    .componentDidUpdate(ctx =>
      Callback {
        ctx.getDOMNode.toElement.foreach { dom =>
          val percent =
            ctx.currentProps.value.toDouble / ctx.currentProps.total.toDouble
          $(dom).progress(
            JsProgressOptions
              .percent(100 * percent)
              .precision(0)
          )
        }
    })
    .componentDidMount(ctx =>
      Callback {
        ctx.getDOMNode.toElement.foreach { dom =>
          val percent =
            ctx.props.value.toDouble / ctx.props.total.toDouble
          $(dom).progress(
            JsProgressOptions
              .percent(100 * percent)
              .precision(0)
          )
        }
    })
    .build

  def apply(p: Props, children: VdomElement*): Unmounted[Props, Unit, Unit] =
    component(p)(children: _*)
}
