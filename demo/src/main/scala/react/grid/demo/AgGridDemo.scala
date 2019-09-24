package react.grid.demo

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.document
import react.aggrid.AgGridReact
import cats.syntax.option._
import seqexec.web.client.semanticui.Size
import seqexec.web.client.semanticui.elements.button.Button
import seqexec.web.client.semanticui.elements.icon.Icon
import seqexec.web.client.semanticui.elements.label.Label

import scala.scalajs.js
import js.JSConverters._

object AgGridStaticDemo {

  type Row = (Car, Int)

  case class Car(make: String, model: String, price: Int)

  private val rowData = List(
    Car("Toyota", "Celica", 35000),
    Car("Ford", "Mondeo", 32000),
    Car("Porsche", "Boxter", 72000)
  )

  private val data = List.fill(100)(rowData).flatten.zipWithIndex

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
  final case class State(selectedRowIdx: Option[Int] = None /*,
                         selectedRowNodeId: Option[String] = None*/ )

  class Backend($ : BackendScope[Props, State]) {

    private var api: Option[AgGridReact.GridApi] = None

    private var selectedRowNodeId: Option[String] = None

    def boldRenderer(f: Row => VdomNode) =
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

    def fancyRenderer(f: Row => VdomNode) =
      ScalaComponent
        .builder[AgGridReact.CellRendererParams[Row]]("ModelCellRenderer")
        .render_P { p =>
          p.data.fold[VdomNode]("Loading...") { row =>
            <.span(
              Label(
                Label.Props("Make:", size = Size.Small, icon = Some(Icon.IconCheckCircleOutline))),
              Button(Button.Props(), f(row))
            )
//          Progress(Progress.Props(f(row).toString, 100, 50, barCls = List.empty))
//            <.b(f(row))
          }
        }
        .build
        .toJsComponent
        .raw

    def conditionalRenderer(f: Row => VdomNode) = {
      val normalRenderer   = boldRenderer(f)
      val selectedRenderer = fancyRenderer(f)
      ScalaComponent
        .builder[AgGridReact.CellRendererParams[Row]]("ModelCellRenderer")
        .render_P { p =>
          println(s"Rendering Node [${p.node.id}]")

          selectedRowNodeId.filter(_ == p.node.id).fold(normalRenderer(p))(_ => selectedRenderer(p))
        }
        .build
        .toJsComponent
        .raw
    }

    private val colDefs = js.Array[AgGridReact.ColDef](
      new AgGridReact.SingleColDef[Row] {
        override val headerName            = "idx"
        override val cellRendererFramework = boldRenderer(_._2)
        override val rowDrag               = true
        override val width                 = 75
      },
      new AgGridReact.SingleColDef[Row] {
        override val headerName            = "Make"
        override val cellRendererFramework = boldRenderer(_._1.make)
      },
      new AgGridReact.SingleColDef[Row] {
        override val headerName            = "Model"
        override val cellRendererFramework = conditionalRenderer(_._1.model)
      },
      new AgGridReact.SingleColDef[Row] {
        override val headerName            = "Price"
        override val cellRendererFramework = boldRenderer(_._1.price)
      }
    )

    private def onGridReady(e: AgGridReact.GridReadyEvent) = Callback {
      println("GRID READY!")
      api = e.api.some
    }

    private def onCellClicked(e: AgGridReact.CellClickedEvent[Row]) =
      $.setState(State(e.rowIndex.some)) >>
        Callback(selectedRowNodeId = e.node.id.some) >>
        Callback(e.api.resetRowHeights()) >>
        Callback(e.api.refreshCells(new AgGridReact.RefreshCellsParams(force = true))) >>
        Callback(e.api.ensureIndexVisible(e.rowIndex, ""))
    //Callback(e.node.setRowHeight(80)) >>
    //Callback(e.api.onRowHeightChanged())

    private def getRowHeight(p: AgGridReact.GetRowHeightParams[Row]): CallbackTo[Int] =
      $.state >>= { s =>
        CallbackTo {
          selectedRowNodeId.filter(_ == p.node.id).fold(40)(_ => 80)
        }
      }

    /*private def onRowDragMove(e: AgGridReact.RowDragMoveEvent[Row]) =
      Callback {
        val draggedData = e.node.data
        println(draggedData)
        e.node.setData(e.overNode.data)
        e.overNode.setData(draggedData)
      }*/

    private def onRowDragEnd(e: AgGridReact.RowDragEndEvent[Row]) =
      Callback {
        println(s"Exchange: ${e.node.data._2} with ${e.overNode.data._2}")
        val draggedData = e.node.data
        e.node.setData(e.overNode.data)
        e.overNode.setData(draggedData)
      }

    def scrollToRow(i: Int) = Callback {
      api.foreach(_.ensureIndexVisible(i, ""))
    }

    val dynamicProps =
      AgGridReact.props(
        columnDefs     = colDefs,
        rowModelType   = AgGridReact.RowModel.Infinite,
        datasource     = datasource,
        cacheBlockSize = blockSize,
        animateRows    = true,
        onGridReady    = onGridReady _,
        onCellClicked  = onCellClicked _,
        //              onRowDragMove  = onRowDragMove _
        onRowDragEnd = onRowDragEnd _
      )

    val staticProps =
      AgGridReact.props(
        defaultColDef = new AgGridReact.SingleColDef[Row] {
          override val resizable = true
        },
        columnDefs = colDefs,
//        rowHeight    = 40,
        rowModelType = AgGridReact.RowModel.ClientSide,
        rowData      = data.toJSArray,
//        rowSelection   = AgGridReact.RowSelection.Single,
        rowDragManaged = true,
        animateRows    = true,
        onGridReady    = onGridReady _,
        onCellClicked  = onCellClicked _,
        getRowHeight   = getRowHeight _
//        onRowDragMove = onRowDragMove _
//        onRowDragEnd = onRowDragEnd _
      )

    def render(p: Props, s: State): VdomElement =
      <.div(
        <.div(^.cls := "ag-theme-balham", ^.height := "600px", ^.width := "1000px")(
          AgGridReact(
//            dynamicProps
            staticProps
          )
        ),
        <.button(^.tpe := "button", ^.onClick --> scrollToRow(75), "Go to row 75"),
        s.selectedRowIdx.whenDefined(row => s"SELECTED ROW IDX: $row  "),
        selectedRowNodeId.whenDefined(id => s"SELECTED ROW NODE ID: $id  ")
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
