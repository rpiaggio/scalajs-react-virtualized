package react
package aggrid

import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Js.RawMounted
import japgolly.scalajs.react.component.Js.UnmountedMapped
import japgolly.scalajs.react.internal.Effect.Id
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.annotation.JSName

object AgGridReact {

  @js.native
  @JSImport("ag-grid-react", "AgGridReact")
  object RawComponent extends js.Object

  @js.native
  trait JsMethods extends js.Object {
    def forceUpdateGrid(): Unit = js.native
    def getOffsetForRow(o: js.Object): Int = js.native
    def measureAllRows(): Unit = js.native
    def recomputeRowHeights(index:  Int): Unit = js.native
    def scrollToPosition(scrollTop: Int): Unit = js.native
    def scrollToRow(index:          Int): Unit = js.native
  }

  // <ColDef>
  // https://www.ag-grid.com/javascript-grid-column-properties/
  trait ColDef extends js.Object {
    val headerName: js.UndefOr[String] = js.undefined
  }

  trait SingleColDef[T] extends ColDef {
    val field: js.UndefOr[String] = js.undefined
    val cellRendererFramework: js.UndefOr[raw.React.ComponentType[CellRendererParams[T]]] =
      js.undefined
    val width: js.UndefOr[Int]       = js.undefined
    val minWidth: js.UndefOr[Int]    = js.undefined
    val maxWidth: js.UndefOr[Int]    = js.undefined
    val rowDrag: js.UndefOr[Boolean] = js.undefined
  }

  trait ColGroupDef extends ColDef {
    val children: js.UndefOr[js.Array[ColDef]] = js.undefined
  }
  // </ColDef>

  sealed trait RowModel {
    val name: String = {
      val (h, t) = toString.splitAt(1)
      h.toLowerCase + t
    }
  }
  object RowModel {
    case object ClientSide extends RowModel
    case object Infinite extends RowModel
    case object ServerSide extends RowModel // Enterprise
    case object Viewport extends RowModel // Enterprise
  }

  @js.native
  trait GetRowsParams[T] extends js.Object {
    var startRow: Int      = js.native
    var endRow: Int        = js.native
    var context: js.Object = js.native
    def successCallback(rowsThisBlock: js.Array[T], lastRow: js.UndefOr[Int] = js.undefined): Unit
    def failCallback(): Unit
  }

  abstract class DataSource[T] extends js.Object {
    def getRows(params: GetRowsParams[T]): Unit
    def destroy(): Unit = {}
  }

  // <RowNode>
  // https://www.ag-grid.com/javascript-grid-row-node/
  @js.native
  trait RowNode[T] extends js.Object {
    val id: String                                   = js.native
    val data: T                                      = js.native
    val parent: js.UndefOr[RowNode[_]]               = js.native
    val level: Int                                   = js.native
    val uiLevel: Int                                 = js.native
    val group: Boolean                               = js.native
    val firstChild: Boolean                          = js.native
    val lastChild: Boolean                           = js.native
    val childIndex: Int                              = js.native
    val rowPinned: js.UndefOr[String]                = js.native
    val canFlower: Boolean                           = js.native
    val childFlower: js.Any                          = js.native // ???
    val stub: Boolean                                = js.native // Enterprise
    val rowHeight: Int                               = js.native
    val rowTop: Int                                  = js.native
    val quickFilterAggregateText: js.UndefOr[String] = js.native

    def setData(newData: T): Unit = js.native
  }
  // </RowNode>

  // <GridAPI>
  // https://www.ag-grid.com/javascript-grid-api/
  @js.native
  trait GridApi extends js.Object {
    // Columns
    def sizeColumnsToFit(): Unit = js.native

    // Scrolling
    def ensureIndexVisible(index: Int, position: String): Unit = js.native
  }
  // </GridAPI>

  // <ColumnAPI>
  // https://www.ag-grid.com/javascript-grid-column-api/
  @js.native
  trait ColumnApi extends js.Object {}
  // </ColumnAPI>

  // <Column>
  // https://www.ag-grid.com/javascript-grid-column/
  @js.native
  trait Column extends js.Object {}
  // </Column>

  // <Events>
  @js.native
  trait AgEvent extends js.Object {
    @JSName("type")
    val tpe: String = js.native
  }

  @js.native
  trait AgGridEvent extends AgEvent {
    val api: GridApi         = js.native
    val columnApi: ColumnApi = js.native
  }

  @js.native
  trait GridReadyEvent extends AgGridEvent

  @js.native
  trait RowEvent[T] extends AgGridEvent {
    val rowNode: RowNode[T]            = js.native
    val data: T                        = js.native
    val rowIndex: Int                  = js.native
    val rowPinned: js.UndefOr[String]  = js.native
    val context: js.UndefOr[js.Object] = js.native
    val event: js.UndefOr[dom.Event]   = js.native
  }

  @js.native
  trait CellEvent[T] extends RowEvent[T] {
    val column: Column          = js.native
    val colDef: SingleColDef[T] = js.native
    val value: js.Any           = js.native
  }

  @js.native
  trait CellClickedEvent[T] extends CellEvent[T]

  @js.native
  trait RowDragEvent[T] extends AgGridEvent {
    val event: dom.MouseEvent          = js.native
    val node: RowNode[T]               = js.native
    val overIndex: Int                 = js.native
    val overNode: RowNode[T]           = js.native
    val y: Int                         = js.native
    val vDirection: js.UndefOr[String] = js.native
  }

  @js.native
  trait RowDragEnterEvent[T] extends RowDragEvent[T]

  @js.native
  trait RowDragMoveEvent[T] extends RowDragEvent[T]

  @js.native
  trait RowDragEndEvent[T] extends RowDragEvent[T]

  @js.native
  trait RowDragLeaveEvent[T] extends RowDragEvent[T]
  // </Events>

  @js.native
  trait CellRendererParams[T] extends js.Object {
    val value: js.Any          = js.native // value to be rendered
    val valueFormatted: js.Any = js.native // value to be rendered formatted
    val getValue
      : Unit => js.Any           = js.native // convenience function to get most recent up to date value
    val setValue: js.Any => Unit = js.native // convenience to set the value
    val formatValue
      : js.Any => js.Any               = js.native // convenience to format a value using the columns formatter
    val data: js.UndefOr[T]            = js.native // the rows data
    val node: RowNode[T]               = js.native // row rows row node
    val colDef: SingleColDef[T]        = js.native // the cells column definition
    val column: Column                 = js.native // the cells column
    val rowIndex: Int                  = js.native // the current index of the row (changes after filter and sort)
    val api: GridApi                   = js.native // the grid API
    val eGridCell: dom.raw.HTMLElement = js.native // the grid's cell, a DOM div element
    val eParentOfValue
      : dom.raw.HTMLElement       = js.native // the parent DOM item for the cell renderer, same as eGridCell unless using checkbox selection
    val columnApi: ColumnApi      = js.native // grid column API
    val context: js.Any           = js.native // the grid's context
    val refreshCell: Unit => Unit = js.native // convenience function to refresh the cell
  }

  // <Props>
  // https://www.ag-grid.com/javascript-grid-properties/
  @js.native
  trait Props extends js.Object {
    // Columns
    var columnDefs: js.UndefOr[js.Array[ColDef]]    = js.native
    var defaultColDef: js.UndefOr[SingleColDef[_]]  = js.native
    var defaultColGroupDef: js.UndefOr[ColGroupDef] = js.native

    // Row Dragging
    var rowDragManaged: js.UndefOr[Boolean] = js.native

    // Data & Row Models
    var rowModelType: js.UndefOr[String]      = js.native
    var rowData: js.UndefOr[js.Array[_]]      = js.native
    var datasource: js.UndefOr[DataSource[_]] = js.native
    var cacheBlockSize: js.UndefOr[Int]       = js.native

    // Rendering & Styling
    var animateRows: js.UndefOr[Boolean] = js.native

    // Events
    var onGridReady: js.UndefOr[js.Function1[GridReadyEvent, Unit]]          = js.native
    var onCellClicked: js.UndefOr[js.Function1[CellClickedEvent[_], Unit]]   = js.native
    var onRowDragEnter: js.UndefOr[js.Function1[RowDragEnterEvent[_], Unit]] = js.native
    var onRowDragMove: js.UndefOr[js.Function1[RowDragMoveEvent[_], Unit]]   = js.native
    var onRowDragEnd: js.UndefOr[js.Function1[RowDragEndEvent[_], Unit]]     = js.native
    var onRowDragLeave: js.UndefOr[js.Function1[RowDragLeaveEvent[_], Unit]] = js.native
  }
  // </Props>

  private def onEvent[E <: AgEvent, E1 <: AgEvent](
    h: js.UndefOr[E => Callback]): js.UndefOr[js.Function1[E1, Unit]] =
    h.map(someH => { e: E1 =>
      someH(e.asInstanceOf[E]).runNow()
    })

  def props[T /* <: js.Object*/ ]( // The row type has to <: js.Object if you want to use AgGrid's renderers, but we don't enforce it.
    columnDefs:         js.UndefOr[js.Array[ColDef]]                 = js.undefined,
    defaultColDef:      js.UndefOr[SingleColDef[T]]                  = js.undefined,
    defaultColGroupDef: js.UndefOr[ColGroupDef]                      = js.undefined,
    rowDragManaged:     js.UndefOr[Boolean]                          = js.undefined,
    rowModelType:       js.UndefOr[RowModel]                         = js.undefined,
    rowData:            js.UndefOr[js.Array[T]]                      = js.undefined,
    datasource:         js.UndefOr[DataSource[T]]                    = js.undefined,
    cacheBlockSize:     js.UndefOr[Int]                              = js.undefined,
    animateRows:        js.UndefOr[Boolean]                          = js.undefined,
    onGridReady:        js.UndefOr[GridReadyEvent => Callback]       = js.undefined,
    onCellClicked:      js.UndefOr[CellClickedEvent[T] => Callback]  = js.undefined,
    onRowDragEnter:     js.UndefOr[RowDragEnterEvent[T] => Callback] = js.undefined,
    onRowDragMove:      js.UndefOr[RowDragMoveEvent[T] => Callback]  = js.undefined,
    onRowDragEnd:       js.UndefOr[RowDragEndEvent[T] => Callback]   = js.undefined,
    onRowDragLeave:     js.UndefOr[RowDragLeaveEvent[T] => Callback] = js.undefined): Props = {
    val p = (new js.Object).asInstanceOf[Props]
    p.columnDefs         = columnDefs
    p.defaultColDef      = defaultColDef
    p.defaultColGroupDef = defaultColGroupDef
    p.rowDragManaged     = rowDragManaged
    p.rowModelType       = rowModelType.map(_.name)
    p.rowData            = rowData
    p.datasource         = datasource
    p.cacheBlockSize     = cacheBlockSize
    p.animateRows        = animateRows
    p.onGridReady        = onEvent[GridReadyEvent, GridReadyEvent](onGridReady)
    p.onCellClicked      = onEvent[CellClickedEvent[T], CellClickedEvent[_]](onCellClicked)
    p.onRowDragEnter     = onEvent[RowDragEnterEvent[T], RowDragEnterEvent[_]](onRowDragEnter)
    p.onRowDragMove      = onEvent[RowDragMoveEvent[T], RowDragMoveEvent[_]](onRowDragMove)
    p.onRowDragEnd       = onEvent[RowDragEndEvent[T], RowDragEndEvent[_]](onRowDragEnd)
    p.onRowDragLeave     = onEvent[RowDragLeaveEvent[T], RowDragLeaveEvent[_]](onRowDragLeave)
    p
  }

  val component = JsComponent[Props, Children.Varargs, Null](RawComponent)
    .addFacade[JsMethods]

  def apply(p: Props)
    : UnmountedMapped[Id, Props, Null, RawMounted[Props, Null] with JsMethods, Props, Null] =
    component.apply(p)()
}
