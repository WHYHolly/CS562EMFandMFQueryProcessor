with NY as(
select cust, avg(quant) as avg_x
from sales
where year = 2004 and state = 'NY'
group by cust),
NJ as(
select cust, avg(quant) as avg_y
from sales
where year = 2004 and state = 'NJ'
group by cust),
CT as(
select cust, avg(quant) as avg_z
from sales
where year = 2004 and state  = 'CT'
group by cust)

select NY.cust, avg_x, avg_y,avg_z
from NY, NJ, CT
where NY.cust = NJ.cust and NY.cust = CT.cust and avg_x > avg_y and avg_x > avg_z