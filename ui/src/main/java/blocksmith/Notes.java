package blocksmith;

/**
 *
 * @author joostmeulenkamp
 */
public class Notes {

}

// Support VarArgs
// - allow varargs as params OR throw exception? 
// - element port to typed ElementPort/PartialInput/...
// - refactor BlockModelFactory to use Block only from creation and PortDefMappingUtils

//
// Seperate UI from Core
// - blocksmith.ui.editormodel
// - cleanup resources folder for core and ui

// IN PROGRESS
// - IN PROGRESS sliders enable min, max and step
//      - DONE Bug: ExecutionInvalidator.invalidateDownstream(...) if(!removed) blocks downstream invalidation for added/new connections
//      - Bug: load days-between not showing error on load
//      - INVESTIGATED: Bug: BigInt and string-to-text not loaded
//      - Cosmetic Bug: blocks with no connections throw exceptions when run, but should not bother users in the UI. The below check takes care of that, but... 
//        also when the below check is used, exceptions are not shown when loading graphs, because the exception is set at block creation, where connection is still not created.
//        behaviour can be fixed in the BlockProjectionAssembler, where the graph is actually still available.
//          MethodBlockNew.updateFrom( ... ) {
//            ...
//            if (!inputPorts.isEmpty() && inputPorts.stream().noneMatch(PortModel::isActive)) {
//                exceptions.clear();
//            }
//            ...
//          }
// - Test if same connection is actually created
//


// TODO
// - UnknownBlocks for missing BlockDefs
// - selectionstate to immutable
// - Copy/Paste (todo paste point)
// - notification - load document error (e.g. when port ids are not found for connections, just omit connection and log) 
// - notification - save document succes
// - block label default values
// - Notification layer
// - handle analogue user input e.g. when user changes slider input, it triggers multiple graph snapshot changes, leading to endless todos
// - clean up - InputControl, MethodBlockNew, PortModel, ConnectionModel



// - TBD - when port inactive, inspection block should not display null, but nothing, add active property or clear method?

//
// Invariants
// - valueId is unique within inputs/params
// - valueId is unique within outputs
// - params only set as strings, so only simple type String is allowed or var type if pass through is needed
// - input ports can only have one incoming connection
// - range default value is "0", lower is "0" and upper "10"
//
//
//
// Questions
// - resizable where to move this? resizable only for specific controls e.g. with resizable interface and only when one is present?
//
//
// REFACTOR BLOCK DEF/FUNC LOADING
// - Object.inspect update toString for Objects
// - ObserveFileBlock -> with SourceBlock
//
// REFACTOR CONTROL INPUT
// - to become extendable (?)
//
//
//
//
// NOTES
// Mouse position is needed when pasting blocks and when creating a new connection 
// Code duplication - Radial Menu, Selection Rectangle and Block Search all test if mouse is on Editor View or Workspace View 
// Check where workspaceController.getView() is used and refactor it
//
// 
//
// TESTS 
// Create connection - Link backward and link forward
// MethodBlock - lacing of lists
// Remove block - remove block and connections
// Auto-create connection undo/redo
//
//
// SMALL UI BUGS
// load spinner causes small method blocks to grow and shrink
// create Paint.color do not move or select, just pick a color, then BlockController.moveCompleted throws an exception
// 
//
// BIG UI BUGS
// Translate scene coordinates to local coordinates of selection rectangle parent container
// 
