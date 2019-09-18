package react.grid.demo

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.document
import react.aggrid.AgGridReact
import cats.syntax.option._

import scala.scalajs.js
import js.JSConverters._

object AgGridStaticDemo {

  case class Row(make: String, model: String, price: Int)

  private val rowData = js.Array[Row](
    Row("Toyota", "Celica", 35000),
    Row("Ford", "Mondeo", 32000),
    Row("Porsche", "Boxter", 72000)
  )

  private val data = List.fill(100)(rowData).flatten

  private val blockSize = 30

  private val datasource = new AgGridReact.DataSource[Row] {
    override def getRows(params: AgGridReact.GetRowsParams[Row]) = {
      println(s"LOADING [${params.startRow}] TO [${params.endRow}]")
      js.timers.setTimeout(500)(
        params.successCallback(
          data.slice(params.startRow, params.endRow).toJSArray,
          Some(data.length).filter(_ - params.endRow < blockSize).orUndefined
        )
      )
      ()
    }
  }


  final case class Props( /*useDynamicRowHeight: Boolean, sortBy: String, s: Size*/ )
  final case class State(selectedRow: Option[Int] = None)

  class Backend($ : BackendScope[Props, State]) {

    private var api: Option[AgGridReact.GridApi] = None

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

    private def onGridReady(e: AgGridReact.GridReadyEvent) = Callback {
      println("GRID READY!")
      api = e.api.some
    }

    private def onCellClicked(e: AgGridReact.CellClickedEvent[Row]) =
        $.setState(State(e.rowIndex.some))

    def scrollToRow(i: Int) = Callback {
      api.foreach(_.ensureIndexVisible(i, ""))
    }

    def render(p: Props, s: State): VdomElement =
      <.div(
        <.div(^.cls := "ag-theme-balham", ^.height := "200px", ^.width := "600px")(
          AgGridReact(
            AgGridReact.props(
              columnDefs     = colDefs,
              rowModelType   = AgGridReact.RowModel.Infinite,
              datasource     = datasource,
              cacheBlockSize = blockSize,
              onGridReady    = onGridReady _,
              onCellClicked  = onCellClicked _
            )
          )
        ),
        <.button(^.tpe := "button", ^.onClick --> scrollToRow(75), "Go to row 75"),
        s.selectedRow.whenDefined(row => s"SELECTED ROW: $row")
      )
  }

  val component = ScalaComponent
    .builder[Props]("AgGridStaticDemo")
    .initialState(State())
    .backend(new Backend(_))
    .renderBackend
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
