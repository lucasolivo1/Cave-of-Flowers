# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer: I did not go to lab, but from the specs, I can see how my project mays differ. My current implementation does not account for tesselation, so it can be very simple.
I will definitely need to implement helper method to check for boundary collision, boundary error, and neighbor locations to place hexagons.
Lucas and I also brute force our way through the hexagon drawing code. I think that I should have break down the (x, y) coordinate in terms of offset and drawing position.
Putting all of them together in one line makes the boundary a bit less clear. It also becomes easy to be messed up by off-by-1 errors.

-----

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer: To me, it seems like hexagons are rooms. Then, we want to connect two hexagons together by tesselating them. In away, this is like hallways.
The difference seem to be that tesselation is built into the coordinate system while hallways are physical objects. However, the similarity still exists in the abstract sense.

-----
**If you were to start working on world generation, what kind of method would you think of writing first?
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer: When designing a world generator, I will work on the room first. This makes the most sense to me since they are the centerpieces of the world.
The hallways can be made to fit the rooms, however, the rooms cannot be made to fit the hallway since they are way too bulky.

-----
**What distinguishes a hallway from a room? How are they similar?**

Answer:A hallway is practically just a skinny room, possibly with turns. A room tends to be more square-ish in shape. Also, it should have interesting contents which a hallway may not have access to.
In general, they are the same because both area reachable and have walls. The bulkiness of the room means that it should be generated first, though.
