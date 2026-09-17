# Clojure Warrior

Write a bot in your own IDE, watch it climb the tower in the browser.

## Run

```sh
npm install
npx shadow-cljs watch app
```

Open http://localhost:8080

Edit `src/warrior/bot.cljs` and save.

## Actions

`play-turn` receives the board and must return one action per turn:

```clojure
[:action/walk :direction/forward]     ; move one space
[:action/attack :direction/forward]   ; attack the adjacent unit (backward attacks at half power)
[:action/shoot :direction/forward]    ; shoot the first unit within 2 spaces
[:action/rescue :direction/forward]   ; free an adjacent captive
[:action/rest]                        ; regain 10% of max health
[:action/pivot]                       ; turn around
```

Directions are `:direction/forward` or `:direction/backward`.

## The Board

The board is a vector of rows, and each space in a row is a map:

```clojure
[[{:unit/type :unit.type/wall}
  {:unit/type :unit.type/warrior
   :unit/health 20.0
   :unit/direction :direction/east
   :unit/melee? true
   :unit/ranged? true}
  {:unit/type :unit.type/floor
   :unit/empty? true}
  {:unit/type :unit.type/sludge
   :unit/health 12.0
   :unit/direction :direction/west
   :unit/enemy? true
   :unit/melee? true}
  {:unit/type :unit.type/stairs
   :unit/stairs? true
   :unit/empty? true}
  {:unit/type :unit.type/wall}]]
```

## Reading the Board

`clojure-warrior.api` (required as `w` in `bot.cljs`) has functions to read
the board. Example results:

```clojure
(w/warrior board)
;; => {:unit/type :unit.type/warrior
;;     :unit/health 20.0
;;     :unit/direction :direction/east
;;     :unit/melee? true
;;     :unit/ranged? true
;;     :unit/position [1 0]}

(w/feel board :direction/forward)
;; the adjacent space in that direction
;; => {:unit/type :unit.type/floor
;;     :unit/empty? true
;;     :unit/position [2 0]}
;; => {:unit/type :unit.type/sludge
;;     :unit/health 12.0
;;     :unit/enemy? true
;;     ...}

(w/look board :direction/forward)
;; the first non-empty space in that direction
;; => {:unit/type :unit.type/archer
;;     :unit/health 7.0
;;     :unit/enemy? true
;;     ...}

(w/listen board)
;; all enemies and captives on the board
;; => ({:unit/type :unit.type/sludge
;;      :unit/enemy? true ...}
;;     {:unit/type :unit.type/captive
;;      :unit/captive? true ...})

(w/stairs board)
;; => {:unit/type :unit.type/stairs
;;     :unit/stairs? true
;;     :unit/empty? true
;;     :unit/position [4 0]}

(w/inspect board [4 0])
;; the space at a position
;; => {:unit/type :unit.type/stairs
;;     :unit/stairs? true
;;     :unit/empty? true
;;     :unit/position [4 0]}

(w/distance-to board [4 0])
;; steps from your warrior to a position
;; => 3

(w/say {:health 20.0})
;; adds {:health 20.0} to the message log
;; also sends its argument to tap>
;; (see them in shadow-cljs Inspect at http://localhost:9630/inspect)
```

## Keeping State Between Turns

`play-turn` is a pure function of the board, so use an atom when you need to
remember something from previous turns (for example, your health last turn):

```clojure
(defonce previous-health (atom nil))

(defn play-turn [board]
  (let [health (:unit/health (w/warrior board))
        taking-damage? (and @previous-health
                            (< health @previous-health))]
    (reset! previous-health health)
    ...))
```

## Development

To run the app with bot in `dev-src/warrior/bot.cljs` instead of `src/warrior/bot.cljs`:

```sh
npm run dev   # shadow-cljs -A:dev watch app
```

Note, dev also expects `clojure-warrior` repo under `checkouts/clojure-warrior`.
