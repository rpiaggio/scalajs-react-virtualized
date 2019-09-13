package react.grid.demo

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.document
import react.virtualized.AgGridReact

import scala.scalajs.js

object AgGridStaticDemo {

  final case class Props( /*useDynamicRowHeight: Boolean, sortBy: String, s: Size*/ )
  final case class State( /*sortDirection: SortDirection, data: List[DataRow]*/ )

  private val colDefs = js.Array[AgGridReact.ColDef](
    new AgGridReact.Col {
      override val headerName = "Make"
      override val field      = "make"
    },
    new AgGridReact.Col {
      override val headerName = "Model"
      override val field      = "model"
    },
    new AgGridReact.Col {
      override val headerName = "Price"
      override val field      = "price"
    }
  )

  val component = ScalaComponent
    .builder[Props]("AgGridStaticDemo")
    .initialState(State( /*SortDirection.ASC, Data.generateRandomList*/ ))
    .renderPS { ($, props, state) =>
      AgGridReact(
        AgGridReact.props(
          columnDefs = colDefs
        )
      )
    }
    .build

  def apply(p: Props) = component(p)
}

object AgGridDemo {
  val component = ScalaComponent
    .builder[Unit]("Demo")
    .stateless
    .render_P { _ =>
      <.div(
        AgGridStaticDemo(AgGridStaticDemo.Props( /*true, "index", s*/ )).vdomElement
      )
    }
    .build

  def main(args: Array[String]): Unit = {
    component().renderIntoDOM(document.getElementById("root"))
    ()
  }
}
