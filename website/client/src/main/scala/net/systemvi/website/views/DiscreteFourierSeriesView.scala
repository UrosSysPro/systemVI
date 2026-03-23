package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom.{CanvasRenderingContext2D, HTMLCanvasElement, console}
import org.scalajs.dom
import io.circe.scalajs.given
import io.circe.generic.auto.*
import io.circe.generic.*

val message = List(Point(-79,-105.5),Point(-79,-106.5),Point(-79,-107.5),Point(-79,-108.5),Point(-79,-110.5),Point(-79,-111.5),Point(-79,-112.5),Point(-79,-114.5),Point(-79,-116.5),Point(-79,-117.5),Point(-79,-119.5),Point(-79,-120.5),Point(-79,-121.5),Point(-79,-122.5),Point(-79,-123.5),Point(-80,-124.5),Point(-81,-125.5),Point(-81,-126.5),Point(-82,-127.5),Point(-83,-127.5),Point(-84,-128.5),Point(-85,-128.5),Point(-86,-128.5),Point(-87,-128.5),Point(-88,-128.5),Point(-89,-128.5),Point(-90,-128.5),Point(-91,-128.5),Point(-92,-128.5),Point(-93,-128.5),Point(-95,-128.5),Point(-96,-127.5),Point(-98,-126.5),Point(-100,-126.5),Point(-102,-125.5),Point(-104,-124.5),Point(-106,-124.5),Point(-108,-123.5),Point(-108,-123.5),Point(-109.00000000000001,-123.16666666666667),Point(-110,-122.83333333333333),Point(-110,-122.83333333333333),Point(-111,-122.3888888888889),Point(-112,-121.94444444444446),Point(-112,-121.94444444444446),Point(-113,-121.46296296296299),Point(-114,-120.9814814814815),Point(-114,-120.9814814814815),Point(-115.00000000000001,-120.48765432098767),Point(-116,-119.99382716049382),Point(-118,-119.5),Point(-119,-118.5),Point(-121,-117.5),Point(-122,-116.5),Point(-123,-116.5),Point(-124,-115.5),Point(-125,-114.5),Point(-125,-113.5),Point(-126,-113.5),Point(-127,-112.5),Point(-127,-111.5),Point(-127,-112.5),Point(-126,-112.5),Point(-126,-114.5),Point(-125,-115.5),Point(-124,-117.5),Point(-123,-118.5),Point(-123,-119.5),Point(-122,-120.5),Point(-122,-121.5),Point(-121,-123.5),Point(-121,-125.5),Point(-120,-127.5),Point(-120,-129.5),Point(-119,-131.5),Point(-119,-133.5),Point(-119,-133.5),Point(-118.66666666666667,-134.5),Point(-118.33333333333333,-135.5),Point(-118.33333333333333,-135.5),Point(-118.22222222222223,-136.5),Point(-118.11111111111111,-137.5),Point(-117,-139.5),Point(-117,-141.5),Point(-117,-142.5),Point(-116,-144.5),Point(-116,-145.5),Point(-116,-147.5),Point(-115,-148.5),Point(-115,-149.5),Point(-115,-150.5),Point(-115,-151.5),Point(-114,-151.5),Point(-114,-152.5),Point(-114,-153.5),Point(-114,-154.5),Point(-114,-155.5),Point(-113,-156.5),Point(-113,-157.5),Point(-113,-158.5),Point(-113,-159.5),Point(-112,-159.5),Point(-112,-160.5),Point(-112,-161.5),Point(-111,-161.5),Point(-111,-162.5),Point(-111,-163.5),Point(-110,-164.5),Point(-110,-165.5),Point(-109,-165.5),Point(-109,-166.5),Point(-109,-167.5),Point(-108,-167.5),Point(-108,-168.5),Point(-108,-169.5),Point(-107,-169.5),Point(-107,-170.5),Point(-106,-170.5),Point(-106,-171.5),Point(-105,-172.5),Point(-105,-173.5),Point(-44,-154.5),Point(-43,-153.5),Point(-42,-153.5),Point(-41,-152.5),Point(-40,-151.5),Point(-40,-150.5),Point(-39,-150.5),Point(-39,-149.5),Point(-39,-147.5),Point(-38,-146.5),Point(-38,-144.5),Point(-38,-142.5),Point(-38,-142.5),Point(-37.66666666666667,-141.5),Point(-37.33333333333333,-140.5),Point(-37.33333333333333,-140.5),Point(-37.25,-139.5),Point(-37.166666666666664,-138.5),Point(-37.08333333333333,-137.5),Point(-37.08333333333333,-137.5),Point(-37.0625,-136.5),Point(-37.041666666666664,-135.5),Point(-37.02083333333333,-134.5),Point(-37.02083333333333,-134.5),Point(-37.015625,-133.5),Point(-37.010416666666664,-132.5),Point(-37.00520833333333,-131.5),Point(-37.00520833333333,-131.5),Point(-37.00390625,-130.5),Point(-37.002604166666664,-129.5),Point(-37.00130208333333,-128.5),Point(-37.00130208333333,-128.5),Point(-37.0009765625,-127.5),Point(-37.000651041666664,-126.5),Point(-37.00032552083333,-125.5),Point(-37.00032552083333,-125.5),Point(-37.00026041666666,-124.5),Point(-37.0001953125,-123.5),Point(-37.00013020833333,-122.5),Point(-37.000065104166666,-121.5),Point(-37.000065104166666,-121.5),Point(-37.33337673611111,-120.50000000000001),Point(-37.66668836805555,-119.5),Point(-37.66668836805555,-119.5),Point(-37.75001627604166,-118.5),Point(-37.83334418402778,-117.5),Point(-37.91667209201389,-116.5),Point(-37.91667209201389,-116.5),Point(-37.9444480613426,-115.5),Point(-37.9722240306713,-114.5),Point(-37.9722240306713,-114.5),Point(-38.648149353780866,-113.5),Point(-39.32407467689043,-112.5),Point(-40,-110.5),Point(-40,-109.5),Point(-40,-108.5),Point(-40,-109.5),Point(-40,-110.5),Point(-40,-111.5),Point(-40,-112.5),Point(-40,-114.5),Point(-40,-115.5),Point(-40,-116.5),Point(-41,-117.5),Point(-41,-118.5),Point(-42,-119.5),Point(-42,-120.5),Point(-43,-120.5),Point(-44,-120.5),Point(-45,-120.5),Point(-46,-120.5),Point(-47,-119.5),Point(-48,-119.5),Point(-49,-119.5),Point(-49,-118.5),Point(-50,-118.5),Point(-51,-117.5),Point(-52,-116.5),Point(-53,-116.5),Point(-53,-115.5),Point(-53,-114.5),Point(-54,-114.5),Point(-54,-113.5),Point(-54,-112.5),Point(-55,-112.5),Point(-55,-111.5),Point(-55,-110.5),Point(-54,-110.5),Point(-54,-109.5),Point(-54,-108.5),Point(-53,-108.5),Point(-52,-108.5),Point(-52,-107.5),Point(-51,-107.5),Point(-50,-106.5),Point(-49,-106.5),Point(-48,-106.5),Point(-47,-106.5),Point(-46,-106.5),Point(-45,-106.5),Point(-44,-106.5),Point(-43,-106.5),Point(-42,-106.5),Point(-41,-106.5),Point(-40,-106.5),Point(-39,-106.5),Point(-38,-106.5),Point(-21,-102.5),Point(-21,-103.5),Point(-19,-104.5),Point(-19,-106.5),Point(-18,-107.5),Point(-17,-108.5),Point(-16,-109.5),Point(-15,-111.5),Point(-14,-113.5),Point(-13,-115.5),Point(-12,-116.5),Point(-11,-118.5),Point(-10,-119.5),Point(-9,-120.5),Point(-9,-122.5),Point(-8,-123.5),Point(-7,-124.5),Point(-7,-125.5),Point(-6,-126.5),Point(-6,-127.5),Point(-5,-128.5),Point(-4,-129.5),Point(-3,-130.5),Point(-2,-131.5),Point(-1,-132.5),Point(0,-133.5),Point(1,-134.5),Point(2,-135.5),Point(3,-136.5),Point(3,-137.5),Point(4,-138.5),Point(5,-138.5),Point(6,-138.5),Point(6,-139.5),Point(7,-139.5),Point(8,-139.5),Point(8,-138.5),Point(9,-137.5),Point(9,-135.5),Point(9,-133.5),Point(9,-131.5),Point(9,-130.5),Point(9,-128.5),Point(9,-126.5),Point(9,-125.5),Point(9,-124.5),Point(9,-123.5),Point(9,-122.5),Point(8,-122.5),Point(8,-121.5),Point(7,-120.5),Point(6,-120.5),Point(5,-119.5),Point(5,-118.5),Point(4,-118.5),Point(3,-117.5),Point(2,-117.5),Point(1,-116.5),Point(0,-116.5),Point(-1,-116.5),Point(-2,-116.5),Point(-3,-116.5),Point(-4,-116.5),Point(-5,-116.5),Point(-6,-116.5),Point(-7,-116.5),Point(-6,-116.5),Point(19,-101.5),Point(20,-101.5),Point(20,-102.5),Point(20,-103.5),Point(21,-104.5),Point(21,-106.5),Point(21,-108.5),Point(21,-109.5),Point(21,-111.5),Point(21,-112.5),Point(21,-114.5),Point(22,-115.5),Point(22,-117.5),Point(23,-118.5),Point(23,-119.5),Point(23,-120.5),Point(24,-121.5),Point(25,-122.5),Point(26,-124.5),Point(26,-125.5),Point(27,-126.5),Point(28,-128.5),Point(28,-129.5),Point(29,-131.5),Point(29,-133.5),Point(30,-134.5),Point(31,-136.5),Point(31,-137.5),Point(32,-138.5),Point(33,-139.5),Point(34,-140.5),Point(35,-140.5),Point(36,-140.5),Point(37,-140.5),Point(38,-140.5),Point(39,-140.5),Point(40,-139.5),Point(40,-138.5),Point(41,-137.5),Point(41,-136.5),Point(41,-134.5),Point(41,-133.5),Point(41,-131.5),Point(41,-130.5),Point(41,-129.5),Point(41,-128.5),Point(40,-127.5),Point(40,-126.5),Point(39,-125.5),Point(38,-124.5),Point(37,-123.5),Point(36,-122.5),Point(35,-121.5),Point(33,-121.5),Point(33,-120.5),Point(32,-119.5),Point(31,-119.5),Point(30,-119.5),Point(29,-118.5),Point(28,-118.5),Point(27,-118.5),Point(26,-118.5),Point(25,-118.5),Point(48,-100.5),Point(49,-100.5),Point(50,-100.5),Point(50,-101.5),Point(51,-101.5),Point(52,-101.5),Point(53,-101.5),Point(54,-102.5),Point(55,-102.5),Point(56,-102.5),Point(57,-102.5),Point(58,-103.5),Point(59,-103.5),Point(60,-103.5),Point(61,-104.5),Point(62,-105.5),Point(63,-106.5),Point(63,-107.5),Point(64,-108.5),Point(65,-109.5),Point(66,-111.5),Point(67,-113.5),Point(67,-114.5),Point(68,-115.5),Point(68,-117.5),Point(69,-118.5),Point(69,-119.5),Point(70,-120.5),Point(71,-122.5),Point(72,-123.5),Point(74,-124.5),Point(75,-125.5),Point(76,-126.5),Point(78,-128.5),Point(79,-129.5),Point(80,-131.5),Point(82,-132.5),Point(82,-133.5),Point(83,-134.5),Point(84,-135.5),Point(85,-136.5),Point(86,-136.5),Point(87,-138.5),Point(88,-139.5),Point(89,-139.5),Point(89,-140.5),Point(90,-140.5),Point(91,-141.5),Point(91,-142.5),Point(92,-142.5),Point(92,-143.5),Point(93,-143.5),Point(93,-144.5),Point(94,-144.5),Point(74,-128.5),Point(74,-129.5),Point(74,-130.5),Point(73,-132.5),Point(72,-133.5),Point(71,-135.5),Point(71,-137.5),Point(70,-139.5),Point(69,-141.5),Point(68,-142.5),Point(68,-144.5),Point(68,-145.5),Point(68,-146.5),Point(68,-147.5),Point(68,-148.5),Point(68,-149.5),Point(67,-150.5),Point(67,-149.5),Point(-83,-54.5),Point(-83,-53.5),Point(-82,-52.5),Point(-82,-50.5),Point(-82,-48.5),Point(-82,-46.5),Point(-82,-46.5),Point(-82,-45.5),Point(-82,-44.5),Point(-82,-44.5),Point(-81.66666666666667,-43.5),Point(-81.33333333333334,-42.5),Point(-81.33333333333334,-42.5),Point(-81.22222222222223,-41.5),Point(-81.11111111111111,-40.5),Point(-81,-38.5),Point(-81,-36.5),Point(-81,-35.5),Point(-81,-33.5),Point(-81,-31.5),Point(-81,-30.5),Point(-81,-28.5),Point(-81,-27.5),Point(-81,-26.5),Point(-80,-24.5),Point(-80,-23.5),Point(-80,-22.5),Point(-80,-20.5),Point(-80,-19.5),Point(-80,-18.5),Point(-79,-17.5),Point(-79,-15.5),Point(-78,-14.5),Point(-78,-13.5),Point(-77,-13.5),Point(-76,-12.5),Point(-76,-11.5),Point(-75,-11.5),Point(-74,-11.5),Point(-73,-10.5),Point(-72,-10.5),Point(-71,-10.5),Point(-70,-10.5),Point(-69,-10.5),Point(-68,-10.5),Point(-67,-11.5),Point(-66,-12.5),Point(-65,-13.5),Point(-64,-14.5),Point(-64,-15.5),Point(-63,-16.5),Point(-62,-16.5),Point(-62,-17.5),Point(-62,-18.5),Point(-62,-19.5),Point(-62,-20.5),Point(-62,-21.5),Point(-62,-22.5),Point(-62,-23.5),Point(-63,-24.5),Point(-63,-25.5),Point(-63,-26.5),Point(-64,-27.5),Point(-64,-28.5),Point(-65,-29.5),Point(-66,-30.5),Point(-67,-30.5),Point(-68,-30.5),Point(-69,-30.5),Point(-70,-31.5),Point(-71,-31.5),Point(-72,-31.5),Point(-73,-31.5),Point(-50,-30.5),Point(-49,-30.5),Point(-48,-30.5),Point(-47,-30.5),Point(-46,-30.5),Point(-45,-30.5),Point(-44,-30.5),Point(-43,-30.5),Point(-42,-30.5),Point(-41,-30.5),Point(-40,-30.5),Point(-39,-30.5),Point(-38,-30.5),Point(10,-61.5),Point(10,-60.5),Point(10,-58.5),Point(10,-57.5),Point(10,-55.5),Point(10,-53.5),Point(10,-53.5),Point(10,-52.5),Point(10,-51.5),Point(10,-51.5),Point(10,-50.5),Point(10,-49.5),Point(10,-48.5),Point(10,-48.5),Point(9.75,-47.5),Point(9.5,-46.5),Point(9.25,-45.5),Point(9.25,-45.5),Point(9.166666666666668,-44.5),Point(9.083333333333334,-43.5),Point(9.083333333333334,-43.5),Point(8.8125,-42.5),Point(8.541666666666668,-41.5),Point(8.270833333333334,-40.5),Point(8.270833333333334,-40.5),Point(7.847222222222223,-39.5),Point(7.423611111111111,-38.5),Point(7.423611111111111,-38.5),Point(7.317708333333333,-37.5),Point(7.211805555555555,-36.5),Point(7.105902777777778,-35.5),Point(7.105902777777778,-35.5),Point(6.829427083333333,-34.5),Point(6.552951388888889,-33.5),Point(6.276475694444445,-32.5),Point(6.276475694444445,-32.5),Point(5.850983796296298,-31.5),Point(5.425491898148149,-30.5),Point(5.425491898148149,-30.5),Point(5.069118923611112,-29.5),Point(4.712745949074074,-28.5),Point(4.356372974537037,-27.5),Point(4.356372974537037,-27.5),Point(3.9042486496913584,-26.5),Point(3.452124324845679,-25.5),Point(3.452124324845679,-25.5),Point(3.0890932436342595,-24.5),Point(2.7260621624228394,-23.5),Point(2.3630310812114197,-22.5),Point(1,-20.5),Point(1,-18.5),Point(0,-17.5),Point(-1,-16.5),Point(-1,-15.5),Point(-2,-14.5),Point(-2,-13.5),Point(-3,-12.5),Point(-4,-12.5),Point(-6,-13.5),Point(-7,-14.5),Point(-8,-15.5),Point(-9,-16.5),Point(-10,-18.5),Point(-11,-19.5),Point(-11,-20.5),Point(-12,-22.5),Point(-13,-23.5),Point(-13,-24.5),Point(-13,-25.5),Point(-13,-26.5),Point(-13,-27.5),Point(-13,-29.5),Point(-13,-30.5),Point(-13,-31.5),Point(-13,-32.5),Point(-13,-33.5),Point(-12,-33.5),Point(-11,-34.5),Point(-11,-35.5),Point(-10,-35.5),Point(-9,-35.5),Point(-8,-35.5),Point(-7,-35.5),Point(-6,-35.5),Point(-5,-35.5),Point(-4,-35.5),Point(-3,-35.5),Point(-2,-35.5),Point(-1,-35.5),Point(27,-41.5),Point(27,-40.5),Point(28,-39.5),Point(28,-37.5),Point(28,-36.5),Point(28,-34.5),Point(29,-32.5),Point(30,-30.5),Point(30,-28.5),Point(30,-28.5),Point(30.000000000000004,-27.500000000000004),Point(30,-26.5),Point(30,-26.5),Point(30.000000000000004,-25.5),Point(30,-24.5),Point(30,-24.5),Point(30.000000000000004,-23.5),Point(30,-22.5),Point(31,-20.5),Point(31,-19.5),Point(31,-18.5),Point(31,-17.5),Point(32,-17.5),Point(31,-17.5),Point(30,-18.5),Point(29,-19.5),Point(28,-20.5),Point(27,-21.5),Point(26,-22.5),Point(25,-22.5),Point(25,-23.5),Point(24,-24.5),Point(23,-24.5),Point(22,-25.5),Point(21,-25.5),Point(20,-25.5),Point(19,-25.5),Point(18,-25.5),Point(18,-24.5),Point(17,-23.5),Point(16,-23.5),Point(15,-22.5),Point(15,-21.5),Point(15,-20.5),Point(14,-19.5),Point(14,-18.5),Point(14,-17.5),Point(14,-16.5),Point(14,-15.5),Point(14,-14.5),Point(15,-14.5),Point(15,-13.5),Point(16,-12.5),Point(16,-11.5),Point(17,-11.5),Point(18,-11.5),Point(19,-11.5),Point(20,-11.5),Point(21,-11.5),Point(22,-11.5),Point(23,-11.5),Point(24,-11.5),Point(25,-11.5),Point(26,-12.5),Point(27,-12.5),Point(48,-8.5),Point(49,-9.5),Point(50,-10.5),Point(50,-11.5),Point(51,-12.5),Point(52,-13.5),Point(53,-15.5),Point(54,-17.5),Point(55,-18.5),Point(56,-20.5),Point(57,-22.5),Point(58,-24.5),Point(59,-26.5),Point(61,-28.5),Point(63,-30.5),Point(65,-32.5),Point(66,-34.5),Point(67,-35.5),Point(68,-37.5),Point(69,-38.5),Point(69,-39.5),Point(55,-25.5),Point(54,-25.5),Point(53,-26.5),Point(53,-27.5),Point(51,-29.5),Point(51,-30.5),Point(50,-32.5),Point(49,-34.5),Point(49,-35.5),Point(49,-37.5),Point(49,-38.5),Point(48,-40.5),Point(48,-41.5),Point(48,-42.5),Point(47,-43.5),Point(47,-44.5),Point(47,-45.5),Point(46,-46.5),Point(-22,54.5),Point(-22,53.5),Point(-22,52.5),Point(-22,51.5),Point(-23,49.5),Point(-23,47.5),Point(-24,46.5),Point(-25,45.5),Point(-25,43.5),Point(-25,42.5),Point(-26,41.5),Point(-26,40.5),Point(-27,39.5),Point(-27,38.5),Point(-28,38.5),Point(-29,38.5),Point(-30,37.5),Point(-31,37.5),Point(-32,36.5),Point(-33,36.5),Point(-34,36.5),Point(-35,36.5),Point(-36,36.5),Point(-37,36.5),Point(-38,36.5),Point(-38,37.5),Point(-39,37.5),Point(-40,37.5),Point(-41,37.5),Point(-42,38.5),Point(-43,39.5),Point(-45,39.5),Point(-46,40.5),Point(-47,40.5),Point(-48,41.5),Point(-49,41.5),Point(-50,42.5),Point(-51,42.5),Point(-52,43.5),Point(-53,43.5),Point(-54,44.5),Point(-54,45.5),Point(-55,45.5),Point(-56,46.5),Point(-57,47.5),Point(-58,48.5),Point(-58,49.5),Point(-58,50.5),Point(-59,51.5),Point(-59,52.5),Point(-59,53.5),Point(-60,54.5),Point(-60,56.5),Point(-60,57.5),Point(-60,59.5),Point(-60,60.5),Point(-60,62.5),Point(-60,63.5),Point(-59,64.5),Point(-59,65.5),Point(-59,66.5),Point(-59,67.5),Point(-58,68.5),Point(-58,69.5),Point(-57,70.5),Point(-57,71.5),Point(-57,72.5),Point(-56,72.5),Point(-56,73.5),Point(-55,74.5),Point(-55,75.5),Point(-55,76.5),Point(-54,77.5),Point(-53,78.5),Point(-53,79.5),Point(-53,80.5),Point(-52,81.5),Point(-51,82.5),Point(-51,83.5),Point(-51,84.5),Point(-50,85.5),Point(-49,86.5),Point(-49,87.5),Point(-48,88.5),Point(-47,89.5),Point(-47,90.5),Point(-47,91.5),Point(-46,91.5),Point(-45,92.5),Point(-45,93.5),Point(-44,95.5),Point(-43,96.5),Point(-42,98.5),Point(-41,100.5),Point(-40,102.5),Point(-40,104.5),Point(-39,106.5),Point(-38,108.5),Point(-37,109.5),Point(-36,111.5),Point(-35,113.5),Point(-34,115.5),Point(-33,116.5),Point(-32,118.5),Point(-32,119.5),Point(-31,120.5),Point(-30,121.5),Point(-30,122.5),Point(-30,123.5),Point(-29,123.5),Point(-18,51.5),Point(-17,50.5),Point(-16,50.5),Point(-15,50.5),Point(-14,49.5),Point(-12,48.5),Point(-11,47.5),Point(-9,46.5),Point(-8,46.5),Point(-7,45.5),Point(-6,45.5),Point(-5,45.5),Point(-4,44.5),Point(-3,44.5),Point(-2,44.5),Point(-1,44.5),Point(0,44.5),Point(1,44.5),Point(2,44.5),Point(3,44.5),Point(3,45.5),Point(4,46.5),Point(4,47.5),Point(5,48.5),Point(5,49.5),Point(5,50.5),Point(5,51.5),Point(5,52.5),Point(5,53.5),Point(5,54.5),Point(5,55.5),Point(5,56.5),Point(5,57.5),Point(5,59.5),Point(4,60.5),Point(4,61.5),Point(4,62.5),Point(3,64.5),Point(3,65.5),Point(3,66.5),Point(2,67.5),Point(2,68.5),Point(1,69.5),Point(1,70.5),Point(0,71.5),Point(0,72.5),Point(-1,72.5),Point(-1,73.5),Point(-1,74.5),Point(-2,74.5),Point(-2,75.5),Point(-3,75.5),Point(-3,76.5),Point(-3,77.5),Point(-4,77.5),Point(-5,78.5),Point(-5,79.5),Point(-6,79.5),Point(-7,80.5),Point(-7,81.5),Point(-7,82.5),Point(-7,83.5),Point(-8,84.5),Point(-9,85.5),Point(-9,86.5),Point(-10,86.5),Point(-10,87.5),Point(-11,88.5),Point(-11,89.5),Point(-12,89.5),Point(-13,90.5),Point(-13,91.5),Point(-14,92.5),Point(-14,93.5),Point(-15,93.5),Point(-15,94.5),Point(-16,95.5),Point(-17,96.5),Point(-17,97.5),Point(-18,98.5),Point(-18,99.5),Point(-19,100.5),Point(-20,101.5),Point(-20,102.5),Point(-21,103.5),Point(-21,104.5),Point(-22,105.5),Point(-22,106.5),Point(-23,107.5),Point(-24,108.5),Point(-24,109.5),Point(-24,110.5),Point(-24,111.5),Point(-25,111.5),Point(-25,112.5),Point(-26,112.5),Point(-26,113.5),Point(-26,114.5),Point(-26,115.5),Point(-26,116.5),Point(-26,117.5),Point(-26,118.5))

case class Complex(re: Double, im: Double):
  def +(o: Complex) = Complex(re + o.re, im + o.im)
  def *(o: Complex) = Complex(re * o.re - im * o.im, re * o.im + im * o.re)
  def magnitude: Double = Math.sqrt(re * re + im * im)
  def phase: Double = Math.atan2(im, re)

object Complex:
  def polar(r: Double, theta: Double) = Complex(r * Math.cos(theta), r * Math.sin(theta))

// One epicycle / Fourier coefficient
case class Epicycle(freq: Int, amplitude: Double, phase: Double)

// Compute DFT → list of epicycles, sorted by amplitude descending
def dft(points: List[Point]): List[Epicycle] =
  val N = points.length
  val phasors = points.map{case Point(x, y) => Complex(x, y)}

  (0 until N).map { k =>
    val sum = phasors.zipWithIndex.foldLeft(Complex(0, 0)) { case (acc, (p, n)) =>
      acc + p * Complex.polar(1.0, -2 * Math.PI * k * n / N)
    }
    val coeff = Complex(sum.re / N, sum.im / N)
    Epicycle(freq = k, amplitude = coeff.magnitude, phase = coeff.phase)
  }.toList.sortBy(-_.amplitude)  // biggest circles first (optional, looks nicer)

// Reconstruct point at t ∈ [0, 1)
def fourierPoint(epicycles: List[Epicycle], t: Double, count:Int = epicycles.length): Point =
  val N = epicycles.length
  val sum = epicycles.take(count).foldLeft(Complex(0, 0)) { case (acc, ep) =>
    acc + Complex.polar(ep.amplitude, ep.phase + 2 * Math.PI * ep.freq * t)
  }
  Point(sum.re, sum.im)

case class MouseState(var x:Int,var y:Int,var down:Boolean)
case class Point(x: Double,y: Double)
def distance(p1:Point,p2:Point) = Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y)).toInt
def square(center:Point,size: Int): List[Point] = {
  val c=Point(center.x-size/2,center.y-size/2)
  val pointsPerSide = 400
  (0 until pointsPerSide).toList.map { i =>
    val p = i / pointsPerSide.toFloat
    Point(c.x+(size * p).toInt, c.y)
  } ++ (0 until pointsPerSide).toList.map { i =>
    val p = i / pointsPerSide.toFloat
    Point(c.x+size,c.y+(size*p).toInt)
  }++ (0 until pointsPerSide).toList.map { i =>
    val p = i / pointsPerSide.toFloat
    Point(c.x+(size*(1f-p)).toInt,c.y+size)
  }++ (0 until pointsPerSide).toList.map { i =>
    val p = i / pointsPerSide.toFloat
    Point(c.x,c.y+(size*(1f-p)).toInt)
  }
}

val backgroundColor = "#515151"
var canvas: HTMLCanvasElement = null
var context: CanvasRenderingContext2D = null

var foregroundCanvas: HTMLCanvasElement = null
var foregroundContext: CanvasRenderingContext2D = null

var mouse = MouseState(0,0,false)
val canvasWidth = 1000
val canvasHeight = 1000
var isDrawing = false
//var points = square(Point(canvasWidth/2,canvasHeight/2),500)
//var points = List.empty[Point]
var points = message
var epicycles = dft(points)
var t = 0d

private def setupCanvas():Unit = {
  canvas.width = canvasWidth
  canvas.height = canvasHeight

  foregroundCanvas.width = canvasWidth
  foregroundCanvas.height = canvasHeight
}

private def loop(timestamp: Double): Unit = {
  if(isDrawing){
    drawPoints(foregroundCanvas,foregroundContext)
  }else {
    val samples = epicycles.length

    foregroundContext.translate(canvas.width / 2, canvas.height / 2)
    val p = fourierPoint(epicycles, t, samples)
    foregroundContext.fillStyle = "#0e7"
    foregroundContext.beginPath()
    foregroundContext.rect(p.x, p.y, 2, 2)
    foregroundContext.fill()
    foregroundContext.translate(-canvas.width / 2, -canvas.height / 2)

    context.fillStyle = "rgba(51,51,51,1)"
    context.lineCap = "round"
    context.rect(0, 0, canvas.width, canvas.height)
    context.fill()
    drawCircles(canvas,context,samples)

    t += 1d / epicycles.length
  }
  dom.window.requestAnimationFrame(loop _)
}

def drawPoints(canvas:HTMLCanvasElement,context:CanvasRenderingContext2D): Unit = {
  context.clearRect(0,0,canvasWidth,canvasHeight)
  context.translate(canvas.width/2,canvas.height/2)
  for(p <- points){
    context.beginPath()
    context.rect(p.x,p.y,2,2)
    context.fillStyle = "#e0f"
    context.fill()
  }
  context.translate(-canvas.width/2,-canvas.height/2)
}

def drawCircles(canvas:HTMLCanvasElement,context:CanvasRenderingContext2D, samples: Int):Unit = {

  context.translate(canvas.width / 2, canvas.height / 2)
  var x = 0d
  var y = 0d
  context.lineWidth = 1
  for (e <- epicycles.take(samples)) {
    context.beginPath()
    context.arc(x, y, e.amplitude, 0, Math.PI * 2)
    context.closePath()
    context.strokeStyle = "#222"
    context.stroke()
    val angle = t * e.freq * Math.PI * 2 + e.phase
    val nx = x + Math.cos(angle) * e.amplitude
    val ny = y + Math.sin(angle) * e.amplitude
    context.beginPath()
    context.moveTo(x, y)
    context.lineTo(nx, ny)
    context.strokeStyle = "#ccc"
    context.stroke()
    x = nx
    y = ny
  }
  context.translate(-canvas.width / 2, -canvas.height / 2)
}

def printPoints(): Unit = {
  var string = "List("
  for(p<-points){
    string ++= s"""Point(${p.x},${p.y}),"""
  }
  string ++= ")"
  dom.console.log(string)
}

def discreteFourierSeriesView(): HtmlElement =  {
  windowEvents(_.onKeyDown).foreach { _ =>
    context.clearRect(0, 0, canvas.width, canvas.height)
    isDrawing = true
  }(unsafeWindowOwner)

  windowEvents(_.onKeyUp).foreach { _ =>
    context.clearRect(0, 0, canvas.width, canvas.height)
    foregroundContext.clearRect(0, 0, canvas.width, canvas.height)
    epicycles = dft(points)
    t = 0
    isDrawing = false
    printPoints()
  }(unsafeWindowOwner)

  div(
    position.relative,
    background("rgba(51,51,51,1)"),
    overflow("hidden"),
    width.percent(100),
    height.vh(100),
    display.flex,
    alignItems.center,
    justifyContent.center,
    canvasTag(
      position.absolute,
      top.percent(50),
      left.percent(50),
      zIndex(2),
      transform("translate(-50%,-50%)"),
      position.absolute,
      width.px(canvasWidth),
      height.px(canvasHeight),
      onMouseDown --> {event =>
        mouse.down = true
      },
      onMouseUp --> {event =>
        mouse.down = false
      },
      onMouseMove --> {event =>
        val x = event.clientX - canvas.getBoundingClientRect().x - canvasWidth/2
        val y = event.clientY - canvas.getBoundingClientRect().y - canvasHeight/2
        if(mouse.down){
          lazy val d = distance(points.last,Point(x,y))
          if(points.nonEmpty && d > 2 && d < 10){
            val last = points.last
            for(i<-0 until d){
              val a = i.toDouble/d
              points :+= Point(last.x*(1f-a)+x*a,last.y*(1f-a)+y*a)
            }
          }else{
            points :+= Point(x, y)
          }
        }
      },
      onMountCallback{ mountContext =>
        foregroundCanvas = mountContext.thisNode.ref
        foregroundContext = foregroundCanvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
      }
    ),
    canvasTag(
      position.absolute,
      top.percent(50),
      left.percent(50),
      transform("translate(-50%,-50%)"),
      width.px(canvasWidth),
      height.px(canvasHeight),
      onMountCallback{ mountContext =>
        canvas = mountContext.thisNode.ref
        context = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
        setupCanvas()
        dom.window.requestAnimationFrame(loop _)
      }
    )
  )
}
