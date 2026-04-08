package blocksmith;

/**
 *
 * @author joostmeulenkamp
 */
public class Notes {

}

// Support VarArgs
// - allow varargs as params OR throw exception? 
// - support within remove multiple blocks 
// - support within paste blocks
// - element port to typed ElementPort/PartialInput/...
// - refactor BlockModelFactory to use Block only from creation and PortDefMappingUtils

//
// Seperate UI from Core
// - blocksmith.ui.editormodel
// - cleanup resources folder for core and ui
// - ensure appRootDir is correctly identified

// IN PROGRESS
// - IN PROGRESS sliders enable mix, max and step
// - DONE Pull-based execution
// - IN PROGESS Refactor for GraphProjectionMapper to not needing to set blocks and connections to active
//      - DONE switch exceptions reading
//      - DONE switch display value reading
//      - DONE add and remove spinner on running
//      - DONE value conversion
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
// - update method executor algorithm
// - Reload block def/func library
// - DONE split project into core, UI and extended lib
// - port data types
// - handle analogue user input e.g. when user changes slider input, it triggers multiple graph snapshot changes, leading to endless todos
// - clean up - InputControl, MethodBlockNew, PortModel, ConnectionModel
// - ExecutionState.valuesOf() return a mutable map (leave it or solve it?), because immutable throws nullpointers with nullable values



// - default "0" for range
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
// - resizable where to move this? textblock was resizable now it is not? resizable only for specific controls e.g. with resizable interface and only when one is present?
//
// Serialization in XML 
// - getBlock from blocks tags feels off as singular
// - naming conflicts with values, blocks, connections
//
// REFACTOR BLOCK DEF/FUNC LOADING
// - Serialization and copy
// - MultilineTextInput update toString for Objects
// - DataSheetBlock
// - ObserveFileBlock -> with SourceBlock
//
// REFACTOR CONTROL INPUT
// - to become extendable (?)
// - to use only strings
//
// BACKLOG
// Domain - replace all JavaFx stuff with pure Java
// Commands - reimplement so logic is not inside commands
//
//
// TODO / WIP
// put stylesheets stuff into AppStylesheetsHelper
//
//
// NOTES
// RemoveSelectedBlocksCommand - removes a block's connections twice
//      - 1) WorkspaceModel.removeConnectionModels(blockModel) removes and returns the removed connections for undo/redo
//      - 2) WorkspaceModel.removeBlockModel(blockModel) removes and returns the removed block for undo/redo. It also removes its connections, but in this case they have already been removed.
// Mouse position is needed when pasting blocks and when creating a new connection 
// Code duplication - Radial Menu, Selection Rectangle and Block Search all test if mouse is on Editor View or Workspace View 
// Check where workspaceController.getView() is used and refactor it
//
// GUIDELINES
// Put everything inside the models that needs to be saved and all business logic needed to make a script work in headless mode
// TODO naming conventions for event handlers & change listeners and placement of handlers / listeners in code
// When a dedicated listener is needed, it should be declared directly above the method it calls, so it easier to find it
// Naming
//            Name	Purpose                                 Implied Behavior                    Good For
//            *Index	Lookup tables, mostly passive           Read-heavy, structure-only          Groups, hierarchies
//            *Registry	Active coordination via registration	Event-aware, dynamic resolution     Plugins, wireless ports
//            *Manager	Heavy orchestration, lifecycles         Stateful control, complex logic     NodeManager, SessionManager
// 
//
// TESTS 
// Create connection - Link backward and link forward
// MethodBlock - lacing of lists
// Remove block - remove block and connections
// Auto-create connection undo/redo
//
// REMINDERS / THOUGTHS
// do block.onIncomingConnectionAdded/Removed make sense, what could their use be? not trigger processing
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
