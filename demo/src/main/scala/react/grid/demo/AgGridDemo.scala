package react.grid.demo

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.document
import react.aggrid.AgGridReact

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

object AgGridStaticDemo {

  final case class Props( /*useDynamicRowHeight: Boolean, sortBy: String, s: Size*/ )
  final case class State( /*sortDirection: SortDirection, data: List[DataRow]*/ )

  @JSExportTopLevel("ModelCellRenderer")
  val ModelCellRenderer = ScalaComponent
    .builder[AgGridReact.CellRendererParams[Row]]("ModelCellRenderer")
    .render_P { p =>
//      println(s"RENDERING CELL! ${JSON.stringify(p.data)}")
    println(p.data)

      <.b(p.data.model)
    }
    .build

  private val colDefs = js.Array[AgGridReact.ColDef](
    new AgGridReact.SingleColDef[Row] {
      override val headerName = "Make"
      override val field      = "make"
    },
    new AgGridReact.SingleColDef[Row] {
      override val headerName            = "Model"
      override val field                 = "model"
      override val cellRendererFramework = ModelCellRenderer.toJsComponent.raw
    },
    new AgGridReact.SingleColDef[Row] {
      override val headerName = "Price"
      override val field      = "price"
    }
  )

//  private class Row(val make: String, val model: String, val price: Int) extends js.Object

  case class Row(make: String, model: String, price: Int)

  private val rowData = js.Array[Row](
    Row("Toyota", "Celica", 35000),
    Row("Ford", "Mondeo", 32000),
    Row("Porsche", "Boxter", 72000)
  )

  val component = ScalaComponent
    .builder[Props]("AgGridStaticDemo")
    .initialState(State( /*SortDirection.ASC, Data.generateRandomList*/ ))
    .renderPS { ($, props, state) =>
      AgGridReact(
        AgGridReact.props(
          columnDefs = colDefs,
//          rowModelType = AgGridReact.RowModel.Infinite,
          rowData = rowData
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
