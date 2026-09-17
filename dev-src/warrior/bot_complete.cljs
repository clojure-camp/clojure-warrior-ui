(ns warrior.bot-complete
  (:require
    [clojure-warrior.api :as w]))

;; Strategy notes:
;; - Enemies never move, so stepping out of a melee enemy's reach is always safe.
;; - Wizards hit for 11 but have 3 health; one shot kills them, so shoot on sight.
;; - Rest to full health whenever outside every enemy's reach and enemies remain.
;; - Pivot only when the next target is an enemy behind us (levels that need it
;;   also grant the pivot ability); backward captives are handled by walking backward.

(def max-health 20.0)
(def low-health 8.0)

(defonce previous-health (atom nil))

(defn x-position [unit]
  (first (:unit/position unit)))

(defn distance-between [unit-a unit-b]
  (abs (- (x-position unit-a) (x-position unit-b))))

(defn direction-toward [warrior unit]
  (let [target-east? (> (x-position unit) (x-position warrior))
        facing-east? (= :direction/east (:unit/direction warrior))]
    (if (= target-east? facing-east?)
      :direction/forward
      :direction/backward)))

(defn opposite-direction [direction]
  (if (= :direction/forward direction)
    :direction/backward
    :direction/forward))

(defn threat-distance [enemy]
  (if (:unit/ranged? enemy)
    2
    1))

(defn threatened? [warrior enemies]
  (some (fn [enemy]
          (<= (distance-between warrior enemy) (threat-distance enemy)))
        enemies))

(defn safe-step? [space]
  (and (:unit/empty? space)
       (not (:unit/stairs? space))))

(defn wizard-shoot-direction [board warrior]
  (->> [:direction/forward :direction/backward]
       (filter (fn [direction]
                 (= :unit.type/wizard
                    (:unit/type (w/look-generic board warrior direction 2)))))
       first))

(defn nearest-to [warrior units]
  (when (seq units)
    (apply min-key
           (fn [unit] (distance-between warrior unit))
           units)))

(defn play-turn [board]
  (let [warrior (w/warrior board)
        health (:unit/health warrior)
        taking-damage? (some-> @previous-health (> health))
        units (w/listen board)
        enemies (filter :unit/enemy? units)
        nearest-unit (nearest-to warrior units)
        nearest-enemy (nearest-to warrior enemies)
        forward-space (w/feel board :direction/forward)
        backward-space (w/feel board :direction/backward)
        adjacent-enemy-direction (cond
                                   (:unit/enemy? forward-space) :direction/forward
                                   (:unit/enemy? backward-space) :direction/backward)
        space-in (fn [direction]
                   (if (= :direction/forward direction)
                     forward-space
                     backward-space))
        retreat-direction (when nearest-enemy
                            (opposite-direction (direction-toward warrior nearest-enemy)))]
    (reset! previous-health health)
    (cond
      (:unit/captive? forward-space)
      [:action/rescue :direction/forward]

      (:unit/captive? backward-space)
      [:action/rescue :direction/backward]

      (wizard-shoot-direction board warrior)
      [:action/shoot (wizard-shoot-direction board warrior)]

      adjacent-enemy-direction
      (let [adjacent-enemy (space-in adjacent-enemy-direction)
            flee-direction (opposite-direction adjacent-enemy-direction)]
        (if (and (:unit/melee? adjacent-enemy)
                 (<= health low-health)
                 (safe-step? (space-in flee-direction)))
          [:action/walk flee-direction]
          [:action/attack adjacent-enemy-direction]))

      (and taking-damage?
           (<= health low-health)
           retreat-direction
           (safe-step? (space-in retreat-direction)))
      [:action/walk retreat-direction]

      (and (< health max-health)
           (seq enemies)
           (not (threatened? warrior enemies)))
      [:action/rest]

      nearest-unit
      (let [travel-direction (direction-toward warrior nearest-unit)]
        (if (and (:unit/enemy? nearest-unit)
                 (= :direction/backward travel-direction))
          [:action/pivot]
          [:action/walk travel-direction]))

      :else
      [:action/walk (direction-toward warrior (w/stairs board))])))
