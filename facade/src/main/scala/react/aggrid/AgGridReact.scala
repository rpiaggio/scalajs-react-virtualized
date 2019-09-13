package react
package virtualized

import japgolly.scalajs.react._
import japgolly.scalajs.react.raw.JsNumber
import japgolly.scalajs.react.component.Js.RawMounted
import japgolly.scalajs.react.component.Js.UnmountedMapped
import japgolly.scalajs.react.internal.Effect.Id

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object AgGridReact {

  type ColumnArg = UnmountedMapped[Id,
                                   Column.Props,
                                   Null,
                                   RawMounted[Column.Props, Null] with Column,
                                   Column.Props,
                                   Null]

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

  trait Col extends ColDef {
    val field: String
  }

  trait ColGroup extends ColDef {
    val children: js.UndefOr[js.Array[ColDef]] = js.undefined
  }
  // </ColDef>

  // <Props>
  // https://www.ag-grid.com/javascript-grid-properties/
  @js.native
  trait Props extends js.Object {
    var columnDefs: js.UndefOr[js.Array[ColDef]] = js.native
    var defaultColDef: js.UndefOr[Col]           = js.native
    var defaultColGroupDef: js.UndefOr[ColGroup] = js.native
    var rowData: js.UndefOr[js.Object]           = js.native
  }
  // </Props>

  def props[C <: js.Object](
    columnDefs:         js.UndefOr[js.Array[ColDef]] = js.undefined,
    defaultColDef:      js.UndefOr[Col]              = js.undefined,
    defaultColGroupDef: js.UndefOr[ColGroup]         = js.undefined,
    rowData:            js.UndefOr[js.Object]        = js.undefined
  ): Props = {
    val p = (new js.Object).asInstanceOf[Props]
    p.columnDefs = columnDefs
    p.defaultColDef = defaultColDef
    p.defaultColGroupDef = defaultColGroupDef
    p.rowData = rowData
    p
  }

  val component = JsComponent[Props, Children.Varargs, Null](RawComponent)
    .addFacade[JsMethods]

  def apply(p: Props /*, children: ColumnArg**/ )
    : UnmountedMapped[Id, Props, Null, RawMounted[Props, Null] with JsMethods, Props, Null] =
    component.apply(p)()
}
