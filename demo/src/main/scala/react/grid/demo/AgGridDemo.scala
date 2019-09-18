package react.grid.demo

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.document
import react.aggrid.AgGridReact

import scala.scalajs.js
import js.JSConverters._

object AgGridStaticDemo {

  final case class Props( /*useDynamicRowHeight: Boolean, sortBy: String, s: Size*/ )
  final case class State( /*sortDirection: SortDirection, data: List[DataRow]*/ )

  def cellRenderer(f: Row => VdomNode) =
    ScalaComponent
      .builder[AgGridReact.CellRendererParams[Row]]("ModelCellRenderer")
      .render_P { p =>
        p.data.fold[VdomNode]("Loading...") { row =>
          <.b(f(row))
        }
      }
      .build
      .toJsComponent
      .raw

  private val colDefs = js.Array[AgGridReact.ColDef](
    new AgGridReact.SingleColDef[Row] {
      override val headerName            = "Make"
      override val cellRendererFramework = cellRenderer(_.make)
    },
    new AgGridReact.SingleColDef[Row] {
      override val headerName            = "Model"
      override val cellRendererFramework = cellRenderer(_.model)
    },
    new AgGridReact.SingleColDef[Row] {
      override val headerName            = "Price"
      override val cellRendererFramework = cellRenderer(_.price)
    }
  )

//  private class Row(val make: String, val model: String, val price: Int) extends js.Object

  case class Row(make: String, model: String, price: Int)

  private val rowData = js.Array[Row](
    Row("Toyota", "Celica", 35000),
    Row("Ford", "Mondeo", 32000),
    Row("Porsche", "Boxter", 72000)
  )

  val data = List.fill(100)(rowData).flatten

  val blockSize = 30

  val datasource = new AgGridReact.DataSource[Row] {
    override def getRows(params: AgGridReact.GetRowsParams[Row]) = {
      println(s"LOADING [${params.startRow}] TO [${params.endRow}]")
      js.timers.setTimeout(500)(
        params.successCallback(
          data.slice(params.startRow, params.endRow).toJSArray,
          Some(data.length).filter(_ - params.endRow < blockSize).orUndefined
        )
      )
    }

    override def destroy() = {}
  }

  val component = ScalaComponent
    .builder[Props]("AgGridStaticDemo")
    .initialState(State( /*SortDirection.ASC, Data.generateRandomList*/ ))
    .renderPS { ($, props, state) =>
      AgGridReact(
        AgGridReact.props(
          columnDefs     = colDefs,
          rowModelType   = AgGridReact.RowModel.Infinite,
          datasource     = datasource,
          cacheBlockSize = blockSize
//          rowData      = rowData
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
