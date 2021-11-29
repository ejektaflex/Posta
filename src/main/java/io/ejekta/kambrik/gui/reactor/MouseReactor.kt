package io.ejekta.kambrik.gui.reactor

open class MouseReactor(
    defaultCanPass: Boolean = false,
) : EventReactor(defaultCanPass) {

    var isHovered: Boolean = false
        private set

    operator fun invoke(func: MouseReactor.() -> Unit) = apply(func)

    open var dragPos = 0 to 0
    open var mouseStartPos: Pair<Int, Int> = 0 to 0

    private var lastMouseDraggedPos = 0 to 0

    var isHeld = false
        private set

//    var isPreSlop = false
//        private set

    // Is true if the item is currently being dragged (past slop distance), false otherwise
    var isDragging: Boolean = false
        private set

    var canDragStart: () -> Boolean = { true }

    var canDragStop: () -> Boolean = { true }

    /**
     * A callback that fires when we start dragging this widget.
     */
    var onDragStart: (relX: Int, relY: Int) -> Unit = { _, _ ->
        // No-op
    }

    /**
     * A callback that fires while the widget is being dragged.
     * Unlike onMouseMoved, this fires even when not hovering the widget.
     */
    var onDragging: (relX: Int, relY: Int) -> Unit = { _, _, ->
        // No-op
    }

    var onDragModify: (relX: Int, relY: Int) -> Pair<Int, Int> = { relX, relY ->
        relX to relY
    }

    /**
     * A callback that fires when we stop dragging this widget.
     * Note: Drag end does not always occur inside the widget bounds!
     */
    var onDragEnd: (relX: Int, relY: Int) -> Unit = { _, _ ->
        // No-op
    }

    /**
     * A callback that fires when the widget is clicked on.
     */
    var onClickDown: (relX: Int, relY: Int, button: Int) -> Unit = { _, _, _ ->
        // No-op
    }

    /**
     * A callback that fires when the mouse is released over the widget.
     */
    var onClickUp: (relX: Int, relY: Int, button: Int) -> Unit = { _, _, _ ->
        // No-op
    }

    /**
     * A callback that fires when the mouse is hovering over the widget.
     * Note: To draw while hovering, use `isHovered` inside the onDraw GUI DSL instead.
     */
    var onHover: (relX: Int, relY: Int) -> Unit = { _, _ ->
        // No-op
    }

    /**
     * A callback that fires when the mouse moves while hovering the widget.
     */
    var onMouseMoved: (relX: Int, relY: Int) -> Unit = { _, _ ->
        // No-op
    }

    var onMouseScrolled: (relX: Int, relY: Int, amount: Double) -> Unit = { _, _, _ ->
        // No-op
    }

    fun doDragStart(relX: Int, relY: Int, absX: Int, absY: Int) {
        isDragging = true
        mouseStartPos = (absX - dragPos.first) to (absY - dragPos.second)
        onDragStart(relX, relY)
    }

    fun doDragging(absX: Int, absY: Int) {
        val modified = onDragModify(
            dragPos.first - absX + mouseStartPos.first,
            dragPos.second - absY + mouseStartPos.first
        )
        val modX = absX + modified.first
        val modY = absY + modified.second
        dragPos = (modX - mouseStartPos.first) to (modY - mouseStartPos.second)
        onDragging(modified.first, modified.second)
    }

    fun doDragStop(relX: Int, relY: Int) {
        isDragging = false
        mouseStartPos = 0 to 0
        onDragEnd(relX, relY)
    }

    fun doClickDown(relX: Int, relY: Int, button: Int) {
        isHeld = true
        onClickDown(relX, relY, button)
    }

    fun doClickUp(relX: Int, relY: Int, button: Int) {
        isHeld = false
        onClickUp(relX, relY, button)
    }

}