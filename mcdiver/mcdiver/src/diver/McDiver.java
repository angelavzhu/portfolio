package diver;

import datastructures.PQueue;
import datastructures.SlowPQueue;
import game.*;
import graph.ShortestPaths;
import java.util.*;

/** This is the place for your implementation of the {@code SewerDiver}.
 */
public class McDiver implements SewerDiver {

    /**
     * See {@code SewerDriver} for specification.
     */
    @Override
    public void seek(SeekState state) {
        // DO NOT WRITE ALL THE CODE HERE. DO NOT MAKE THIS METHOD RECURSIVE.
        // Instead, write your method (it may be recursive) elsewhere, with a
        // good specification, and call it from this one.
        //
        // Working this way provides you with flexibility. For example, write
        // one basic method, which always works. Then, make a method that is a
        // copy of the first one and try to optimize in that second one.
        // If you don't succeed, you can always use the first one.
        //
        // Use this same process on the second method, scram.
        HashSet<Long> visited = new HashSet<>();
        optWalk(state, visited);


    }

    /**
     * Helper method for seek. Starts from the current location of 'state' and searches for the shortest path
     * to the ring by moving McDiver to the neighbor that is closest to the ring. Stores visited node IDs in
     * 'visited'. Requires 'state' and 'visited' are not null.
     */
    private void optWalk(SeekState state, HashSet<Long> visited) {
        assert state != null && visited != null;
        if (state.distanceToRing() == 0) {
            return;
        }
        long originalLocation = state.currentLocation();
        visited.add(state.currentLocation());
        PriorityQueue<NodeStatus> intersection = new PriorityQueue<>((n1, n2)
                -> n1.getDistanceToRing() - n2.getDistanceToRing());
        for (NodeStatus n : state.neighbors()) {
            intersection.add(n);
        }
        while (!intersection.isEmpty()) {
            NodeStatus n = intersection.poll();
            if (!visited.contains(n.getId())) {
                state.moveTo(n.getId());
                optWalk(state, visited);
                if (state.distanceToRing() == 0) {
                    return;
                }
                state.moveTo(originalLocation);
            }
        }
    }

    /**
     * See {@code SewerDriver} for specification.
     */
    @Override
    public void scram(ScramState state) {
        // TODO: Get out of the sewer system before the steps are used up.
        // DO NOT WRITE ALL THE CODE HERE. Instead, write your method elsewhere,
        // with a good specification, and call it from this one.
        Maze m = new Maze((Set<Node>) state.allNodes());
        ShortestPaths<Node, Edge> s = new ShortestPaths<>(m);
        finalScramHelper3(state,s);
    }

    /**
     * Helper method for scram. Moves McDiver to the next best tile based on the best ratio of coin value to
     * distance from McDiver's current location. If McDiver is unable to reach the best node within
     * 'stepsToGo', McDiver heads straight for the exit.
     */

    public void finalScramHelper3(ScramState state, ShortestPaths<Node, Edge> paths) {
        //queue of all the nodes in the maze ordered by ratio of coins:distance to node
        PriorityQueue<Node> bestTiles = buildQueue(state,paths);

        //distance from location to next
        double distToNode;
        //distance from next to exit
        double distToExit;

        //add all nodes to priority queue
        for (Node n : state.allNodes()) {
            bestTiles.add(n);
        }

        Node next = bestTiles.poll();

        //get distance from current node to next
        paths.singleSourceDistances(state.currentNode());
        distToNode = paths.getDistance(next);

        //get distance from next to exit
        paths.singleSourceDistances(next);
        distToExit = paths.getDistance(state.exit());

        //while McDiver can get to the exit within available steps, take next best node out of priority queue
        while (distToNode + distToExit <= state.stepsToGo() && distToNode != 0) {
            paths.singleSourceDistances(state.currentNode());
            for (Edge e : paths.bestPath(next)) {
                state.moveTo(e.getOther(e.source()));
            }
            // rebuild queue from new current location
            bestTiles = buildQueue(state,paths);

            //remove the first node from the queue (best ratio)
            next = bestTiles.poll();

            //get distance from current node to next
            paths.singleSourceDistances(state.currentNode());
            distToNode = paths.getDistance(next);

            //get distance from next to exit
            paths.singleSourceDistances(next);
            distToExit = paths.getDistance(state.exit());

        }
        // move McDiver to exit
        paths.singleSourceDistances(state.currentNode());
        for (Edge e : paths.bestPath(state.exit())) {
            state.moveTo(e.getOther(e.source()));
        }
    }

    /**
     * Builds a priority queue of all nodes in 'state'. Priority is based on the ratio of coin value to
     * distance to current node. Requires state and paths are not null.
     *
     */
    private PriorityQueue<Node> buildQueue (ScramState state, ShortestPaths<Node, Edge> paths) {
        //queue of all the nodes in the maze ordered by ratio of coins:distance to node
        assert state != null && paths != null;
        PriorityQueue<Node> bestTiles = new PriorityQueue<>((n1, n2) -> {
            //run djikstra on current
            paths.singleSourceDistances(state.currentNode());
            //get distance from current to n1
            double distOne = paths.getDistance(n1);
            //run djikstra on n2
            //paths.singleSourceDistances(state.currentNode());
            //get distance from current to n2
            double distTwo = paths.getDistance(n2);

            Tile n1Tile = n1.getTile();
            Tile n2Tile = n2.getTile();

            double ratio1 = n1Tile.coins()/distOne;
            double ratio2 = n2Tile.coins()/distTwo;

            return (int) (ratio2 - ratio1);
        });

        //add all nodes to priority queue
        for (Node n : state.allNodes()) {
                bestTiles.add(n);
        }

        return bestTiles;

    }

}