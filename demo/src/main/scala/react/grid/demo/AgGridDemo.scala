package react.grid.demo

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.document
import react.virtualized.AgGridReact

import scala.scalajs.js
import scala.scalajs.js.UndefOr

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

  private class Row(val make: String, val model: String, val price: Int) extends js.Object

  private val rowData = js.Array[Row](
    new Row("Toyota", "Celica", 35000),
    new Row("Ford", "Mondeo", 32000),
    new Row("Porsche", "Boxter", 72000)
  )

  val component = ScalaComponent
    .builder[Props]("AgGridStaticDemo")
    .initialState(State( /*SortDirection.ASC, Data.generateRandomList*/ ))
    .renderPS { ($, props, state) =>
      AgGridReact(
        AgGridReact.props(
          columnDefs = colDefs,
          rowData = UndefOr.any2undefOrA(rowData)
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
      <.div(^.cls := "ag-theme-balham", ^.height := "200px", ^.width := "600px")(
        AgGridStaticDemo(AgGridStaticDemo.Props( /*true, "index", s*/ )).vdomElement
      )
    }
    .build

  def main(args: Array[String]): Unit = {
    component().renderIntoDOM(document.getElementById("root"))
    ()
  }
}
