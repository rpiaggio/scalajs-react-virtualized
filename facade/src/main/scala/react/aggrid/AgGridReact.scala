package react
package aggrid

import japgolly.scalajs.react._
import japgolly.scalajs.react.raw.JsNumber
import japgolly.scalajs.react.component.Js.RawMounted
import japgolly.scalajs.react.component.Js.UnmountedMapped
import japgolly.scalajs.react.internal.Effect.Id
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object AgGridReact {

  @js.native
  @JSImport("ag-grid-react", "AgGridReact")
  object RawComponent extends js.Object

  @js.native
  trait JsMethods extends js.Object {
    def forceUpdateGrid(): Unit = js.native
    def getOffsetForRow(o: js.Object): JsNumber = js.native
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
    val field: String
    val cellRendererFramework: js.UndefOr[raw.React.ComponentType[CellRendererParams[T]]] =
      js.undefined
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
  trait GetRowsParams extends js.Object {
    var startRow: JsNumber = js.native
    var endRow: JsNumber   = js.native
    var context: js.Object = js.native
    def successCallback(rowsThisBlock: js.Array[js.Object], lastRow: JsNumber): Unit
    def failCallback(): Unit
  }

  trait DataSource {
    def getRows(params: GetRowsParams): Unit
    def destroy(): Unit = {}
  }

  // <RowNode>
  // https://www.ag-grid.com/javascript-grid-row-node/
  @js.native
  trait RowNode[T] extends js.Object {
    val id: String                                   = js.native
    val data: T                                      = js.native
    val parent: js.UndefOr[RowNode[_]]               = js.native
    val level: JsNumber                              = js.native
    val uiLevel: JsNumber                            = js.native
    val group: Boolean                               = js.native
    val firstChild: Boolean                          = js.native
    val lastChild: Boolean                           = js.native
    val childIndex: JsNumber                         = js.native
    val rowPinned: js.UndefOr[String]                = js.native
    val canFlower: Boolean                           = js.native
    val childFlower: js.Any                          = js.native // ???
    val stub: Boolean                                = js.native // Enterprise
    val rowHeight: JsNumber                          = js.native
    val rowTop: JsNumber                             = js.native
    val quickFilterAggregateText: js.UndefOr[String] = js.native
  }
  // </RowNode>

  // <GridAPI>
  // https://www.ag-grid.com/javascript-grid-api/
  @js.native
  trait GridApi extends js.Object {
    // Columns
    def sizeColumnsToFit(): Unit = js.native

    // Scrolling
    def ensureIndexVisible(index: JsNumber, position: String): Unit = js.native
  }
  // </GridAPI>

  // <ColumnAPI>
  // https://www.ag-grid.com/javascript-grid-column-api/
  @js.native
  trait ColumnApi extends js.Object {}
  // </ColumnAPI>

  @js.native
  trait Column extends js.Object {}

  @js.native
  trait CellRendererParams[T] extends js.Object {
    val value: js.Any          = js.native // value to be rendered
    val valueFormatted: js.Any = js.native // value to be rendered formatted
    val getValue
      : Unit => js.Any           = js.native // convenience function to get most recent up to date value
    val setValue: js.Any => Unit = js.native // convenience to set the value
    val formatValue
      : js.Any => js.Any        = js.native // convenience to format a value using the columns formatter
    val data: T                 = js.native // the rows data
    val node: RowNode[T]        = js.native // row rows row node
    val colDef: SingleColDef[T] = js.native // the cells column definition
    val column: Column          = js.native // the cells column
    val rowIndex
      : JsNumber                       = js.native // the current index of the row (changes after filter and sort)
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

    // Data & Row Models
    var rowModelType: js.UndefOr[String]   = js.native
    var rowData: js.UndefOr[js.Array[_]]   = js.native
    var datasource: js.UndefOr[DataSource] = js.native
  }
  // </Props>

  def props[T /* <: js.Object*/ ]( // The row type has to <: js.Object if you want to use AgGrid's renderers, but we don't enforce it.
                                  columnDefs:         js.UndefOr[js.Array[ColDef]] = js.undefined,
                                  defaultColDef:      js.UndefOr[SingleColDef[T]]  = js.undefined,
                                  defaultColGroupDef: js.UndefOr[ColGroupDef]      = js.undefined,
                                  rowModelType:       js.UndefOr[RowModel]         = js.undefined,
                                  rowData:            js.UndefOr[js.Array[T]]      = js.undefined,
                                  datasource:         js.UndefOr[DataSource]       = js.undefined): Props = {
    val p = (new js.Object).asInstanceOf[Props]
    p.columnDefs         = columnDefs
    p.defaultColDef      = defaultColDef
    p.defaultColGroupDef = defaultColGroupDef
    p.rowModelType       = rowModelType.map(_.name)
    p.rowData            = rowData
    p.datasource         = datasource
    p
  }

  val component = JsComponent[Props, Children.Varargs, Null](RawComponent)
    .addFacade[JsMethods]

  def apply(p: Props /*, children: ColumnArg**/ )
    : UnmountedMapped[Id, Props, Null, RawMounted[Props, Null] with JsMethods, Props, Null] =
    component.apply(p)()
}
